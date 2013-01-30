package nl.helixsoft.bridgedb;

import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

import junit.framework.TestCase;

public class TestIdentifiersOrgFormatter extends TestCase
{
	public void setUp()
	{
		BioDataSource.init();
	}
	
	public void test1()
	{
		Xref ref = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		IdentifiersOrgFormatter idOrg = new IdentifiersOrgFormatter();
		String uri = idOrg.format(ref);
		assertEquals ("http://identifiers.org/ncbigene/3643", uri);
	}	
}
