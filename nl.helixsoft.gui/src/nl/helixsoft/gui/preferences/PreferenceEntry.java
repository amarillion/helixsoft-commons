package nl.helixsoft.gui.preferences;

import java.awt.Color;
import java.io.File;

/**
 * Entry from a preference Table.
 * <p>
 * Combines the Preference Key with a reference to the table containing the Preference current value.
 */
public interface PreferenceEntry 
{
	public String get();
	public void set(String value);

	public int getInt();
	public void setInt(Integer value);
	public File getFile ();
	public void setFile (File value);
	public Color getColor ();
	public void setColor (Color value);
	public void setBoolean (Boolean value);
	public boolean getBoolean ();

}
