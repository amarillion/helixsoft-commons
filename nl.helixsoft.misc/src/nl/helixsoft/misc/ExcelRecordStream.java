package nl.helixsoft.misc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import nl.helixsoft.misc.impl.HssfRecordStream;
import nl.helixsoft.recordstream.RecordStream;

public class ExcelRecordStream 
{
	public static class Builder
	{
		private File file;
		public String sheet;
		
		public RecordStream get() throws IOException
		{
			try {
				if (file.getName().endsWith(".xls"))
				{
					RecordStream result = new HssfRecordStream(file, sheet);
					return result;
				}
				else if (file.getName().endsWith(".xlsx"))
				{
//					XssfRecordStream result = new XssfRecordStream(file, sheet);
					Class<?> myClass = Class.forName("nl.helixsoft.misc.impl.XssfRecordStream");
					RecordStream result = (RecordStream)myClass.getConstructor(File.class, String.class).newInstance(file, sheet);
					return result;
				}
				else
				{
					throw new IOException ("Unrecognized extension, must be either xlsx or xls " + file.getName());
				}
			} catch (ClassNotFoundException e) {
				throw new IOException ("Could not create excel record stream", e);
			} catch (InstantiationException e) {
				throw new IOException ("Could not create excel record stream", e);
			} catch (IllegalAccessException e) {
				throw new IOException ("Could not create excel record stream", e);
			} catch (IllegalArgumentException e) {
				throw new IOException ("Could not create excel record stream", e);
			} catch (InvocationTargetException e) {
				throw new IOException ("Could not create excel record stream", e);
			} catch (NoSuchMethodException e) {
				throw new IOException ("Could not create excel record stream", e);
			} catch (SecurityException e) {
				throw new IOException ("Could not create excel record stream", e);
			}				
			
		}
	}
	
	public static Builder open(File xlsFile, String sheet)
	{
		Builder builder = new Builder();
		builder.file = xlsFile;
		builder.sheet = sheet;
		return  builder;
	}
	
}
