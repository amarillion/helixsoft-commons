package nl.helixsoft.bridgedb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.helixsoft.util.StringUtils;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

/**
 * Converts Xrefs to identifiers.org URI's.
 * <p>
 * Identifiers.org URI's have the nice property that they are
 * resolveable.
 * <p>
 * For Xrefs that are not in the miriam registry
 * (for example, Affymetrix id's), it is not possible to generate
 * an identifiers.org URI. Instead a URI is generated that starts 
 * with http://bridgedb.org. These are unfortunately not resolveable.
 */
public class IdentifiersOrgFormatter implements XrefFormatter 
{
	Pattern pat = Pattern.compile("urn:miriam:(.+):(.+)");
	Pattern rev = Pattern.compile("http://(bridgedb|identifiers).org/(.+)/(.+)");
	
	@Override
	public String format(Xref ref) 
	{
		String urn = ref.getURN();
		Matcher mat = pat.matcher(urn);
		
		if (mat.matches())
		{
			return mat.replaceFirst ("http://identifiers.org/$1/$2");
		}
		else
		{
			String idEncoded = StringUtils.urlEncode(ref.getId());
			String dsEncoded = StringUtils.urlEncode(ref.getDataSource().getFullName());
			
//			System.out.println ("WARNING: " + ref + " " + ref.getDataSource().getFullName());
			return "http://bridgedb.org/" + dsEncoded + "/" + idEncoded;
		}
	}
	
}
