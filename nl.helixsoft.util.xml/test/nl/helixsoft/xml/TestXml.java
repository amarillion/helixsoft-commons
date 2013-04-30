package nl.helixsoft.xml;

import nl.helixsoft.xml.Xml;
import junit.framework.TestCase;

public class TestXml extends TestCase 
{
	public void test1()
	{
		String s = Xml.elt("book").setAttr("author", "douglas adams").add(Xml.elt("title", "Hitchhikers guide to the galaxy")).toString();
		assertEquals ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<book author=\"douglas adams\">\n  <title>Hitchhikers guide to the galaxy</title>\n</book>", s);
	}
	
}
