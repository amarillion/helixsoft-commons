package nl.helixsoft.util.gui;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.pathvisio.core.preferences.Preference;
import org.pathvisio.core.preferences.PreferenceEvent;
import org.pathvisio.core.preferences.PreferenceListener;

import nl.helixsoft.util.gui.ColorConverter;

/**
 * Adapted from PathVisio.
 * <p>
 * Loads & saves application preferences
 */
public class PreferenceManager
{
	private final File propertiesFile;
	
	public PreferenceManager (File propertiesFile)
	{
		this.propertiesFile = propertiesFile;
	}
	
	private Properties properties;
	private Set<PreferenceListener> listeners = new HashSet<PreferenceListener>();
	private boolean dirty;


	public void addListener(PreferenceListener listener) {
		listeners.add(listener);
	}

	private void fireEvent(Preference modifiedPref) {
		PreferenceEvent event = new PreferenceEvent(modifiedPref);
		for (PreferenceListener l : listeners) {
			l.preferenceModified(event);
		}
	}

	/**
	 * Stores preferences back to preference file, if necessary.
	 * Only writes to disk if the properties have changed.
	 */
	public void store()
	{
		if (dirty)
		{
			System.out.println ("Preferences have changed. Writing preferences");
			try
			{
				properties.store(new FileOutputStream(propertiesFile), "");
				dirty = false;
			}
			catch (IOException e)
			{
				System.err.println ("Could not write properties");
			}
		}
	}

	/**
	 * Load preferences from file
	 */
	public void load()
	{
		properties = new Properties();
		try
		{
			if(propertiesFile.exists()) {
				properties.load(new FileInputStream(propertiesFile));
			} else {
				System.out.println ("Preferences file " + propertiesFile + " doesn't exist, using defaults");
			}
		}
		catch (IOException e)
		{
			System.err.println ("Could not read properties");
			e.printStackTrace();
		}
		dirty = false;
	}

	/**
	 * Get a preference as String
	 */
	public String get (Preference p)
	{
		String key = p.name();
		if (properties.containsKey(key))
		{
			return properties.getProperty(key);
		}
		else
		{
			return p.getDefault();
		}
	}

	public void set (Preference p, String newVal)
	{
		String oldVal = get(p);

		if (oldVal == null ? newVal == null : oldVal.equals (newVal))
		{
			// newVal is equal to oldVal, do nothing
		}
		else
		{
			if (newVal == null)
				properties.remove(p.name());
			else
				properties.setProperty(p.name(), newVal);
			fireEvent(p);
			dirty = true;
		}
	}

	public int getInt (Preference p)
	{
		return Integer.parseInt (get(p));
	}

	public void setInt (Preference p, int val)
	{
		set (p, "" + val);
	}

	public File getFile (Preference p)
	{
		return new File (get (p));
	}

	public void setFile (Preference p, File val)
	{
		set (p, "" + val);
	}

	public Color getColor (Preference p)
	{
		return ColorConverter.parseColorString(get (p));
	}

	public void setColor (Preference p, Color c)
	{
		set (p, ColorConverter.getRgbString(c));
	}

	public void setBoolean (Preference p, Boolean val)
	{
		set (p, "" + val);
	}

	public boolean getBoolean (Preference p)
	{
		return (get(p).equals (""  + true));
	}

	/**
	 * Returns true if the current value of Preference p equals the default value.
	 */
	public boolean isDefault (Preference p)
	{
		return !properties.containsKey(p.name());
	}

}
