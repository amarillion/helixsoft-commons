package nl.helixsoft.gui.preferences;

import java.awt.Color;
import java.io.File;


/**
 * Entry from a preference Table.
 * <p>
 * Combines the Preference Key with a reference to the table containing the Preference current value.
 */
/* package */ class PreferenceEntryImpl implements PreferenceEntry
{
	final PreferenceManager parent;
	final Preference key;
	
	/* package */ PreferenceEntryImpl (PreferenceManager parent, Preference key)
	{
		this.parent = parent;
		this.key = key;
	}
	
	@Override
	public String get() { return parent.get(key); }

	@Override
	public int getInt() { return parent.getInt(key); }

	@Override
	public File getFile() { return parent.getFile(key); }

	@Override
	public Color getColor() { return parent.getColor(key); }


	@Override
	public boolean getBoolean() { return parent.getBoolean(key); }

	@Override
	public void setInt(Integer value) { parent.setInt(key, value); }
	
	@Override
	public void setFile(File value) { parent.setFile(key, value); }
	
	@Override
	public void setColor(Color value) { parent.setColor(key, value); }

	@Override
	public void setBoolean(Boolean value) { parent.setBoolean (key, value); }

	@Override
	public void set(String value) { parent.set (key, value); }
}
