package nl.helixsoft.gui;

import nl.helixsoft.gui.preferences.Preference;


/**
 * Set of preferences for the main application window.
 */
public enum AppPreference implements Preference 
{
	/** Main window left coordinate */ WIN_X(50),
	/** Main window top coordinate */ WIN_Y(50),
	/** Main window width */ WIN_W(800),
	/** Main window height */ WIN_H(600);

	private String defaultValue;
	
	private AppPreference(Object o)
	{
		defaultValue = "" + o;
	}
	
	@Override
	public String getDefault() 
	{
		return defaultValue;
	}

}