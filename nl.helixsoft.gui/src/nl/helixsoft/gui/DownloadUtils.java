package nl.helixsoft.gui;

import java.awt.GraphicsEnvironment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import javax.swing.ProgressMonitorInputStream;

public class DownloadUtils 
{
	//TODO: add option for silent downloading, or for a CLI progress monitor.
	public static void downloadFile(URL url, File dest) throws IOException
	{
		System.out.println ("Please wait while downloading bridge database from " + url);
		
		File temp = File.createTempFile(dest.getName(), ".tmp", dest.getParentFile());
		
		try
		{
			OutputStream out = new BufferedOutputStream(
					new FileOutputStream(temp));
			URLConnection conn = url.openConnection();
			
			// For websites that provide different downloads depending on language - it's rare, but Oryzabase (http://www.shigen.nig.ac.jp/) does this.
			conn.setRequestProperty("Accept-Language", Locale.getDefault().getISO3Language());
			
			InputStream in = conn.getInputStream();
	
			ProgressMonitorInputStream pin = new ProgressMonitorInputStream(
					null,
					"Downloading " + url,
					in
			);
			pin.getProgressMonitor().setMillisToDecideToPopup(0);
			pin.getProgressMonitor().setMaximum(conn.getContentLength());
	
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = pin.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
	
			in.close();
			out.close();
			temp.renameTo(dest);
		}
		finally
		{
			temp.delete();
		}
		
		
	}

	public static boolean isHeadLess()
	{
		return GraphicsEnvironment.isHeadless();
	}
	
    public static void printProgBar(int percent){
        StringBuilder bar = new StringBuilder("[");

        for(int i = 0; i < 50; i++){
            if( i < (percent/2)){
                bar.append("=");
            }else if( i == (percent/2)){
                bar.append(">");
            }else{
                bar.append(" ");
            }
        }

        bar.append("]   " + percent + "%     ");
        System.out.print("\r" + bar.toString());
    }
}
