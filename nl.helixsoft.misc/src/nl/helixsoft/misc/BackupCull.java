package nl.helixsoft.misc;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Utility for culling old backup files.
 * <p>
 * Takes a list of files as input. The files are grouped by age. For each age-group one file is kept, the rest is discarded.
 * This way you only keep a small collection of historic backups.
 * <p>
 * TODO
 * [ ] Provide command-line options to adjust the bin cut-offs
 * [ ] Allow working on dated directories instead of files
 * [ ] Provide command-line option to extract date from filename instead of file creation time.
 * [ ] Better unit testing.
 */
public class BackupCull 
{
	/**
	 * Comparator used to decide which file to keep from a Bin.
	 * We give preference to certain dates, i.e. the first day of the month, on the first month of the year, etc.
	 */
	private static class PreferredFileComparator implements Comparator <File>
	{
		public int score (File a)
		{
			int score = 0;
			LocalDate t = new LocalDate (a.lastModified());
			int dom = t.getDayOfMonth(); 
			if ((dom % 7) == 1) score += 5; // prefer days 1, 8, 15, 22, 29
			if (dom == 1) score += 5; // prefer day 1 even more
			int moy = t.getMonthOfYear();
			if ((moy %3) == 1) score++; // prefer months 1, 4, 7, 10
			if (moy == 1) score++; // prefer month 1 even more
			return score;
		}
		
		public int compare(File a, File b)
		{
			// From Java 7: Integer.compare (score(a), score(b))
			return Integer.valueOf(score(b)).compareTo(Integer.valueOf(score(a)));
		}
	}

	/**
	 * A Bin is a collection of Files below a certain age.
	 * Bins are chained - Files that fall below a cut-off are passed to the next bin.
	 */
	private static class Bin
	{
		/**
		 * With andThen... we can create a chain of Bins.
		 */
		public Bin andThen (LocalDate d)
		{
			next = new Bin();
			next.end = d;
			return next;
		}
		
		/**
		 * Try to add a File to this bin. If it's too old, it's automatically passed to the next bin.
		 */
		public void addFile(File f)
		{
			LocalDate ftime = new LocalDate (f.lastModified());
			if (ftime.compareTo(end) < 0)
			{
				if (next != null)
				{
					next.addFile (f);
				}
			}
			else
			{
				files.add (f);
			}
		}

		/**
		 * @param dryrun enables dry-run mode, the actions are printed but not actually executed. 
		 */
		public void execute(boolean dryrun)
		{
			// keep the first File in the priorityQueue (it has the highest score). The others are marked for deletion.
			boolean first = true;
			for (File f : files)
			{
				if (first)
				{
					if (dryrun) System.out.println ("    KEEP " + f);
				}
				else
				{
					if (dryrun)
					{
						System.out.println ("    REM  " + f);
					}
					else
					{
						System.out.print ("DELETE " + f);
						// perform actual delete
						if (!f.delete()) { System.out.print ("-> FAILED"); }
						System.out.println();
					}
				}
				first = false;
			}
			if (dryrun) System.out.println (end);
			
			if (next != null)
			{
				next.execute(dryrun);
			}
		}
		
		Bin next;
		LocalDate end;
		PriorityQueue<File> files = new PriorityQueue<File>(16, new PreferredFileComparator());
	}

	/** Command-line options */
	public static class Options
	{
		@Option(name="-n", usage="Dry run. Show actions, but do not execute.")
		boolean dryRun = false;

		@Option(name="--help", aliases="-h", usage="Show usage")
		boolean help = false;
		
		@Argument(usage="Files to cull")
		List<File> files;
	}
	
	Options opts;
	
	/** All the work starts here */
	public void run(String[] args)
	{
		opts = new Options();
	    CmdLineParser parser = new CmdLineParser(opts);
	    
	    try
	    {
	    	parser.parseArgument(args);
	    	if (opts.help) throw new CmdLineException (parser, "Help requested");
	    	if (opts.files == null) throw new CmdLineException (parser, "Expected at least one file");
	    }
	    catch (CmdLineException ex)
	    {
	        System.err.println(ex.getMessage());
	        
	        parser.printUsage(System.err);
	        return;
	    }

		LocalDate today = new LocalDate();

		Bin chain = new Bin ();
		chain.end = today;
		chain.
			andThen (today.minus(Period.days(3))).
		    andThen (today.minus(Period.days(7))).
			andThen (today.minus(Period.months(1))).
			andThen (today.minus(Period.months(3))).
			andThen (today.minus(Period.years(1))).
			andThen (today.minus(Period.years(2))).
			andThen (today.minus(Period.years(5)));
		
		for (File f : opts.files)
		{
			chain.addFile (f);
		}

		chain.execute(opts.dryRun);
	}
	
	public static void main(String[] args)
	{
		new BackupCull().run(args);
	}
}
