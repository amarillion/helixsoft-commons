package nl.helixsoft.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class BasicHyperlinkListener implements HyperlinkListener
{
	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0)
	{
		if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse(arg0.getURL().toURI());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
