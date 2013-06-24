package nl.helixsoft.gui;

import java.awt.Color;

/**
 * Methods for writing and parsing colors in different ways.
 * There are two methods for exchanging RGB triplets in a 255,255,255 string format,
 * and two methods for exchanging RGB triplets as
 * a <color red="255" green="255" blue="255"/> JDom element
 * <p>
 * Adapted from PathVisio, with modifications.
 */
public abstract class ColorConverter
{
	/**
	 * Returns a string representing a {@link Color} object.
	 * @param c The {@link Color} to be converted to a string
	 * @return a string representing the {@link Color} c
	 */
	public static String getRgbString(Color c) {
		return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
	}

	/**
	 * Parses a string representing a {@link Color} object created with {@link #getRgbString(Color)}
	 * @param rgbString the string to be parsed
	 * @return the {@link Color} object this string represented
	 */
	public static java.awt.Color parseColorString(String colorString)
	{
		String[] s = colorString.split(",");
		try
		{
			return new java.awt.Color(
					Integer.parseInt(s[0]),
					Integer.parseInt(s[1]),
					Integer.parseInt(s[2]));
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Unable to parse color from '" + colorString + "'", e);
		}
	}

    final static String XML_ELEMENT_COLOR = "color";
	final static String XML_COLOR_R = "red";
	final static String XML_COLOR_G = "green";
	final static String XML_COLOR_B = "blue";
}
