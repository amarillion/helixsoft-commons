package nl.helixsoft.util;

import junit.framework.TestCase;

public class TestURLParser extends TestCase 
{

	public void test1()
	{
		String test = "/sparql?default-graph-uri=&query=SELECT+*%0D%0AWHERE%7B%0D%0ASERVICE+%3Chttp%3A%2F%2Fmard01.gb.local%2Fsparql%2Funiprot%2F%3E%0D%0A%7B%7B%0D%0ASELECT+*%0D%0AWHERE%0D%0A%7B%0D%0A%3Fprotein+a+uniprot%3AProtein+.+%3Fprotein+uniprot%3Acitation+%3Fc+.+%3Fc+owl%3AsameAs+%3Fx%0D%0A%7D+LIMIT+1000%0D%0A+%7D%7D%0D%0A%7D+LIMIT+1000&format=text%2Fhtml&timeout=0&debug=on";
		
		URLParser p = new URLParser(test);
		assertEquals ("/sparql", p.getBase());
		assertEquals ("", p.getParam("default-graph-uri"));
		assertNotNull (p.getParam("query"));
	}
	
	
}
