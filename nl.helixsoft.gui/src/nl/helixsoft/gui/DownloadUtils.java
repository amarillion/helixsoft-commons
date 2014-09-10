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

import javax.swing.ProgressMonitorInputStream;

import nl.helixsoft.util.FileUtils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class DownloadUtils 
{
	//TODO: add option for silent downloading, or for a CLI progress monitor.
	public static void downloadFile(URL url, File dest) throws IOException
	{
		// use apache client for FTP to work around 2G size limit in java.
		// https://community.oracle.com/thread/1144802?start=0&tstart=0
		if ("ftp".equals (url.getProtocol()))
		{
			downloadFtp (url, dest);
		}
		else
		{
			URLConnection conn = url.openConnection();
			downloadFile (conn, dest);
		}
	}

	public static void downloadFtp(URL url, File dest) throws IOException
	{
		FTPClient client = new FTPClient();
	    client.connect(url.getHost());
	    
        String password = System.getProperty("user.name")+ "@" + FileUtils.safeMachineName("unknown");
        client.login("anonymous", password);

	    int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            client.disconnect();
            throw new IOException ("FTP server refused connection with code: " + reply);
        }
        
//	    fos = new FileOutputStream(dest);
        
        //TODO - how to determine correct file type?
        client.setFileType(FTPClient.BINARY_FILE_TYPE);
        
        // passive mode is more robust
        // see http://www.jscape.com/blog/bid/80512/Active-v-s-Passive-FTP-Simplified
        // TODO - according to some, both active and passive should be tried in turns...
        client.enterLocalPassiveMode();

     	FTPFile[] stat = client.listFiles(url.getPath());
     	
     	long size;
     	if (stat.length != 1) 
     	{
//     		client.disconnect();
//     		throw new IOException ("Could not stat " + url);
     		System.err.println ("WARNING: could not stat " + url + "\nlength: " + stat.length + "\nreply: " + client.getReplyString());
     		size = -1;
     	}
     	else
     	{
     		size = stat[0].getSize();
     	}
	    InputStream inputStream = client.retrieveFileStream(url.getPath());
	    downloadFile (inputStream, dest, url, size);
//	    
//	    client.retrieveFile("/" + url.getPath(), fos);
//	    fos.close();
	    client.disconnect();
	}

	public static void downloadStream (URLConnection conn, OutputStream out) throws IOException
	{
		downloadStream (conn.getInputStream(), out, conn.getURL(), conn.getContentLength() /* from java 1.7: getContentLengthLong() */ );
	}
	
	//TODO: add option for silent downloading, or for a CLI progress monitor.
	/**
	 * Download from URLconnection to an output stream.
	 * Show progress dialog.
	 * @param conn
	 * @param out
	 * @throws IOException
	 */
	public static void downloadStream (InputStream in, OutputStream out, URL url, long contentLength) throws IOException
	{
		System.out.println ("Please wait while downloading file from " + url);
		
		InputStream xin;
		if (!GraphicsEnvironment.isHeadless())
		{
			ProgressMonitorInputStream pin = new ProgressMonitorInputStream(
					null,
					"Downloading " + url,
					in
			);
			pin.getProgressMonitor().setMillisToDecideToPopup(0);
			pin.getProgressMonitor().setMaximum((int)contentLength); //TODO - potential for int overflow
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

	public static void downloadFile(URLConnection conn, File dest) throws IOException
	{		
		downloadFile (conn.getInputStream(), dest, conn.getURL(), conn.getContentLength() /* from java 1.7: getContentLengthLong() */ );
	}
	
	//TODO: add option for silent downloading, or for a CLI progress monitor.
	/**
	 * Download from URLConnection into a File.
	 * If the destination file already exists, an exception is thrown.
	 * This function uses a temporary intermediate file, so if there is an error during download, no file is created.
	 *  
	 * The URLConnection form allows setting extra request headers, such as for HTTP Basic Authentication etc.
	 */
	public static void downloadFile(InputStream in, File dest, URL url, long contentLength) throws IOException
	{		
		if (dest.exists()) throw new IOException ("File " + dest + " already exists");
		File temp = File.createTempFile(dest.getName(), ".tmp", dest.getParentFile());	
		try
		{
			OutputStream out = new BufferedOutputStream(
					new FileOutputStream(temp));
			downloadStream (in, out, url, contentLength);
						
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
