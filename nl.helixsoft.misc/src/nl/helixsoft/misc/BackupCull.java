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
 * Utility for culling old backup files
 */
public class BackupCull 
{
	private static class PreferredFileComparator implements Comparator <File>
	{
		public int score (File a)
		{
			int score = 0;
			LocalDate t = new LocalDate (a.lastModified());
			int dom = t.getDayOfMonth(); 
			if ((dom % 7) == 1) score += 5;
			if (dom == 1) score += 5;
			int moy = t.getMonthOfYear();
			if ((moy %3) == 1) score++;
			if (moy == 1) score++;
			return score;
		}
		
		public int compare(File a, File b)
		{
			return Integer.compare(score(b), score(a));
		}
	}

	private static class Bin
	{
		public Bin andThen (LocalDate d)
		{
			next = new Bin();
			next.end = d;
			return next;
		}
		
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

		public void execute()
		{
			execute (true);
		}
		
		public void execute(boolean dryrun)
		{
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
	
	public void run(String[] args)
	{
		opts = new Options();
	    CmdLineParser parser = new CmdLineParser(opts);
	    
		// depending on task, dump rdf, generate provenance rdf, or execute tasks.
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
			andThen (today.minus(Period.years(2)));
		
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
