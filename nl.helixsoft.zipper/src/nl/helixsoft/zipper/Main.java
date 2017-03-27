package nl.helixsoft.zipper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import nl.helixsoft.util.HFileUtils;
import nl.helixsoft.util.HStringUtils;


class Main 
{
	static final String HELP = 
			  "Zipper is a tool that creates zip / tar.gz bundles "
			+ "according to instructions\nfrom an ini-style configuration file.\n"
			+ "\n"
			+ "Zipper lets you:\n"
			+ "* define the name of the top-level zip entry\n"
			+ "* select files to include using glob patterns\n"
			+ "* create multiple bundles from a single config file\n"
			+ "* put zip entries in a different relative location\n"
			+ "* generate precisely the same tar.gz and zip bundles\n"
			+ "* use macro variables using C-style #include and #define\n"
			+ "\n";
					  
	public static class Mapping
	{
		public String srcGlob;
		public String dest;
		
		// not parsed but managed during processing
		public List<File> _files = new ArrayList<File>();
	}

	public static class Config
	{	
		public String suffix;
		public String section;
		public List<Mapping> mappings = new ArrayList<Mapping>();
		
		// not parsed but managed during processing		
		public File _destZip;
		public File _destTgz;
	}
	
	public static class Project
	{
		public String baseName;
		public String version;
		public String baseDir;
		public List<Config> configurations = new ArrayList<Config>();
	}
	
	private void readInclude(File inFile, Map<String, String> props) throws IOException
	{
		BufferedReader reader = new BufferedReader (new FileReader(inFile));
		String raw;
		while ((raw = reader.readLine()) != null)
		{
			Pattern patDefine = Pattern.compile("^#define\\s+(\\w+)\\s+(.*)");
			
			String line = raw.trim();
			if ("".equals(line)) continue;
			
			Matcher m0 = patDefine.matcher(line); 
			if (m0.matches()) 
			{
				String key = m0.group(1);
				String value = HStringUtils.removeOptionalQuotes(m0.group(2));
				props.put(key, value);
			}
		}
		
		reader.close();
	}
	
	private String substProps(String in, Map<String, String> props)
	{
		String result = in;
		for (Map.Entry<String, String> entry : props.entrySet())
		{
			String var = "${" + entry.getKey() + "}";
			result = Pattern.compile(var, Pattern.LITERAL).matcher(result).replaceAll(entry.getValue());
		}
		return result;
	}
	
	private Project parseConfig(File inFile) throws IOException
	{
		BufferedReader reader = new BufferedReader (new FileReader(inFile));
		
		try
		{
			Config currentConfig = null;
			
			Map<String, String> props = new HashMap<String, String>();
			Project result = new Project();
			
			Pattern patSection = Pattern.compile("\\[(.*)\\]");
			Pattern patProperty = Pattern.compile("(.*\\S)\\s*=\\s*(.*)");
			Pattern patInclude = Pattern.compile("^#include\\s+'(.*)'");
			Pattern patComment = Pattern.compile("^#.*");
			Pattern patMapping = Pattern.compile("(.*\\S)\\s*->\\s*(.*)");
			
			String raw;
			int lineno = 0;
			while ((raw = reader.readLine()) != null)
			{
				lineno++;
				String line = raw.trim();
				if ("".equals(line)) continue;
				
				Matcher m0 = patInclude.matcher(line); 
				if (m0.matches())
				{
					readInclude(new File(inFile.getParentFile(), m0.group(1)), props);
					continue;
				}
				
				if (patComment.matcher(line).matches()) continue;
				
				Matcher m1 = patSection.matcher(line); 
				if (m1.matches())
				{
					currentConfig = new Config();
					currentConfig.section = m1.group(1);
					result.configurations.add(currentConfig);
					continue;
				}
				
				Matcher m2 = patProperty.matcher(line);
				if (m2.matches())
				{
					String key = m2.group(1);
					String value = substProps(m2.group(2), props);
					
					if ("version".equals(key))
					{
						result.version = value;
					}
					else if ("basename".equals(key))
					{
						result.baseName = value;
					}
					else if ("basedir".equals(key))
					{
						result.baseDir = value;
					}
					else if ("suffix".equals(key))
					{
						if (currentConfig == null)
						{
							throw new IllegalStateException("suffix must not be in main section: " + lineno);
						}
						
						currentConfig.suffix = value;
					}				
					else
					{
						throw new IllegalStateException("Invalid key found in config file: " + key + ", in line " + lineno);
					}
					
					continue;
				}
				
				if (currentConfig == null)
				{
					throw new IllegalStateException("Found pattern in main section in line " + lineno);
				}
				
				Mapping mapping = new Mapping();
				Matcher m3 = patMapping.matcher(line);
				if (m3.matches())
				{
					mapping.dest = m3.group(2);
					mapping.srcGlob = m3.group(1);
				}			
				else
				{
					mapping.dest = null; // use default.
					mapping.srcGlob = line;
				}
				currentConfig.mappings.add(mapping);
				
			}
			return result;
		
		}
		finally
		{
			reader.close();
		}
		
	}
	
	/** Command-line options */
	public static class Options
	{
		@Option(name="-c", usage="Configuration file")
		File configFile = new File ("zipper.conf");

		@Option(name="--help", aliases="-h", usage="Show usage")
		boolean help = false;

		@Option(name="--version", aliases="-v", usage="Print version and quit")
		boolean version = false;

		@Option(name="--format", aliases="-f", usage="which formats to produce. Valid values: tgz, zip or both. Default: both")
		String format = "both";

		@Argument(usage="run selected sections only")
		List<String> sections;
	}
	Options opts = new Options();

	boolean formatTgz = true;
	boolean formatZip = true;
	
	private void checkFormat(String fmt) throws CmdLineException
	{
		if ("both".equals(fmt))
		{
			formatTgz = true;
			formatZip = true;
		}
		else if ("tgz".equals(fmt))
		{
			formatTgz = true;
			formatZip = false;
		}
		else if ("zip".equals(fmt))
		{
			formatTgz = false;
			formatZip = true;
		}
		else
		{
			throw new CmdLineException("Unknown format selected: " + fmt + " must be one of 'tgz', 'zip' or 'both'");
		}
	}
	
	public void run(String[] args) throws IOException
	{		
	    CmdLineParser parser = new CmdLineParser(opts);
	    
	    try
	    {
	    	parser.parseArgument(args);
	    	if (opts.help) throw new CmdLineException (parser, "Help requested");
	    	if (opts.version) {
	    	
	    		InputStream is = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
	    		if (is != null)
	    		{
	    			Manifest manifest = new Manifest (is);
	    			System.out.println("buildDate: " + manifest.getMainAttributes().getValue("Build-Date"));
	    			System.out.println("gitHash: " +  manifest.getMainAttributes().getValue("Git-Hash"));
	    		}
	    		return;
	    	}
	    	
	    	checkFormat(opts.format);
	    	
	    	if (!opts.configFile.exists()) throw new CmdLineException("Could not find configuration file " + opts.configFile);
	    }
	    catch (CmdLineException ex)
	    {
	        System.err.println(ex.getMessage());
	        
	        System.out.println(HELP);
	        parser.printUsage(System.out);
	        return;
	    }
	
		File inFile = new File ("zipper.conf");
		// step 1: read configuration file.
		
		Project project = parseConfig(inFile);
		doProject(project);
	}

	private boolean isValidSection(Config config)
	{
		return (opts.sections == null || opts.sections.contains(config.section));
	}
	
	private void doProject(Project project) throws IOException
	{
		for (Config config : project.configurations)
		{
			if (isValidSection(config)) processConfig(project, config);
		}
		
		for (Config config : project.configurations)
		{
			if (isValidSection(config)) 
			{
				if (formatZip) doConfigZip(project, config);
				if (formatTgz) doConfigTgz(project, config);
			}
		}
	}
	
	private void processConfig(Project project, Config config)
	{
		// create Zip archive...
		config._destZip = new File (project.baseName + "-" + config.suffix + "-" + project.version + ".zip");
		config._destTgz = new File (project.baseName + "-" + config.suffix + "-" + project.version + ".tar.gz");
		
		for (Mapping pat : config.mappings)
		{
			pat._files = HFileUtils.expandGlob(pat.srcGlob);
			if (pat._files.size() == 0) throw new IllegalStateException ("Glob " + pat.srcGlob + " matches 0 files");
			
			if (pat.dest == null)
			{
				File globAsFile = new File(pat.srcGlob).getParentFile();
				if (globAsFile != null)
				{
					pat.dest = project.baseDir + File.separator + globAsFile;
				}
				else
				{
					pat.dest = project.baseDir;
				}
			}
		}

	}
	
	
	private void doConfigZip(Project project, Config config) throws IOException
	{		
		ensureParentDirExists(config._destZip);
		
		System.out.println("Writing " + config._destZip);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(config._destZip));		
		for (Mapping pat : config.mappings)
		{
			for (File f : pat._files)
			{				
//				System.out.println ("Adding " + f.getName() + " as " + pat.dest + "/" + f.getName());
				ZipEntry ze = new ZipEntry(pat.dest + "/" + f.getName());
				zos.putNextEntry(ze);
				FileInputStream fis = new FileInputStream(f);
				IOUtils.copy(fis, zos);
				zos.closeEntry();;
			}
		}
		
		zos.close();
	}

	private void ensureParentDirExists(File destFile) {
		
		File parentFile = destFile.getParentFile();
		if (parentFile != null)
		{
			parentFile.mkdirs();
		}
	}

	private void doConfigTgz(Project project, Config config) throws IOException
	{		
		ensureParentDirExists(config._destTgz);
		
		FileOutputStream fos = new FileOutputStream(config._destTgz);
		TarArchiveOutputStream zos = new TarArchiveOutputStream(new GZIPOutputStream (fos));
		
		System.out.println("Writing " + config._destTgz);
		for (Mapping pat : config.mappings)
		{
			for (File f : pat._files)
			{	
//				System.out.println ("Adding " + f.getName() + " as " + pat.dest + "/" + f.getName());
				TarArchiveEntry ze = new TarArchiveEntry(f, pat.dest + "/" + f.getName());
				if (f.canExecute())
				{
					ze.setMode(0755);
				}
				
				zos.putArchiveEntry(ze);
				FileInputStream fis = new FileInputStream(f);
				try
				{
					IOUtils.copy(fis, zos);
				}
				finally
				{
					fis.close();
				}
				zos.closeArchiveEntry();
			}
		}
		
		zos.close();
		fos.close();
	}

	public static void main(String[] args) throws IOException 
	{ new Main().run(args); }
}
