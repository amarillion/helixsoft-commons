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
		URLConnection conn = url.openConnection();
		downloadFile (conn, dest);
	}

	//TODO: add option for silent downloading, or for a CLI progress monitor.
	/**
	 * Download from URLconnection to an output stream.
	 * Show progress dialog.
	 * @param conn
	 * @param out
	 * @throws IOException
	 */
	public static void downloadStream (URLConnection conn, OutputStream out) throws IOException
	{
		System.out.println ("Please wait while downloading file from " + conn.getURL());
		// For websites that provide different downloads depending on language - it's rare, but Oryzabase (http://www.shigen.nig.ac.jp/) does this.
		// TODO: move this bit to Oryzabase parser and use URLConnection form
		conn.setRequestProperty("Accept-Language", Locale.getDefault().getISO3Language());
		
		InputStream in = conn.getInputStream();
		
		InputStream xin;
		if (!GraphicsEnvironment.isHeadless())
		{
			ProgressMonitorInputStream pin = new ProgressMonitorInputStream(
					null,
					"Downloading " + conn.getURL(),
					in
			);
			pin.getProgressMonitor().setMillisToDecideToPopup(0);
			pin.getProgressMonitor().setMaximum(conn.getContentLength());
			xin = pin;
		}
		else
		{
			//TODO: use printProgBar as alternative
			xin = in;
		}

		byte[] buffer = new byte[1024];
		int numRead;
		long numWritten = 0;
		while ((numRead = xin.read(buffer)) != -1) {
			out.write(buffer, 0, numRead);
			numWritten += numRead;
		}

		in.close();
		out.flush();	
	}
	
	//TODO: add option for silent downloading, or for a CLI progress monitor.
	/**
	 * Download from URLConnection into a File.
	 * If the destination file already exists, an exception is thrown.
	 * This function uses a temporary intermediate file, so if there is an error during download, no file is created.
	 *  
	 * The URLConnection form allows setting extra request headers, such as for HTTP Basic Authentication etc.
	 */
	public static void downloadFile(URLConnection conn, File dest) throws IOException
	{		
		if (dest.exists()) throw new IOException ("File " + dest + " already exists");
		File temp = File.createTempFile(dest.getName(), ".tmp", dest.getParentFile());	
		try
		{
			OutputStream out = new BufferedOutputStream(
					new FileOutputStream(temp));
			downloadStream (conn, out);
						
			out.close();
			if (!temp.renameTo(dest))
			{
				throw new IOException ("Could not rename " + temp + " to " + dest);
			}
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
