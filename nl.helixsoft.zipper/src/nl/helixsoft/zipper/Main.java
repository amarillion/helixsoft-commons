package nl.helixsoft.zipper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import nl.helixsoft.util.HFileUtils;


class Main 
{
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
	
	private Project parseConfig(File inFile) throws IOException
	{
		BufferedReader reader = new BufferedReader (new FileReader(inFile));
		
		try
		{
			
			String currentSection = null;
			Config currentConfig = null;
			
			Project result = new Project();
			
			Pattern patSection = Pattern.compile("\\[(.*)\\]");
			Pattern patProperty = Pattern.compile("(.*\\S)\\s*=\\s*(.*)");
			Pattern patComment = Pattern.compile("^#.*");
			Pattern patMapping = Pattern.compile("(.*\\S)\\s*->\\s*(.*)");
			
			String raw;
			int lineno = 0;
			while ((raw = reader.readLine()) != null)
			{
				lineno++;
				String line = raw.trim();
				if ("".equals(line)) continue;
				if (patComment.matcher(line).matches()) continue;
				
				Matcher m1 = patSection.matcher(line); 
				if (m1.matches())
				{
					currentSection = m1.group(1);
					currentConfig = new Config();
					result.configurations.add(currentConfig);
					continue;
				}
				
				Matcher m2 = patProperty.matcher(line);
				if (m2.matches())
				{
					String key = m2.group(1);
					String value = m2.group(2);
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
						if (currentSection == null)
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
				
				if (currentSection == null)
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
	
	public void run(String[] args) throws IOException
	{		
		File inFile = new File ("zipper.conf");
		// step 1: read configuration file.
		
		Project project = parseConfig(inFile);
				
		doProject(project);
	}

	private void doProject(Project project) throws IOException
	{
		for (Config config : project.configurations)
		{
			processConfig(project, config);
		}
		
		for (Config config : project.configurations)
		{
			doConfigZip(project, config);
			doConfigTgz(project, config);
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
		
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(config._destZip));		
		for (Mapping pat : config.mappings)
		{
			for (File f : pat._files)
			{				
				System.out.println ("Adding " + f.getName() + " as " + pat.dest + "/" + f.getName());
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
		
		for (Mapping pat : config.mappings)
		{
			for (File f : pat._files)
			{	
				System.out.println ("Adding " + f.getName() + " as " + pat.dest + "/" + f.getName());
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
