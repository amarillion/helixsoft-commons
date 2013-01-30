package nl.helixsoft.bridgedb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.helixsoft.util.StringUtils;

import org.bridgedb.Xref;

public class IdentifiersOrgFormatter implements XrefFormatter 
{
	Pattern pat = Pattern.compile("urn:miriam:(.+):(.+)");
	
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
