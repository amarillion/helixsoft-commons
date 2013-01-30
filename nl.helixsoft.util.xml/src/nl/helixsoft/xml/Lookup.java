package nl.helixsoft.xml;

public class Lookup implements HtmlRenderable
{
	private final String expr;

	public Lookup (String expr)
	{
		this.expr = expr;
	}

	@Override
	public void flush(Context c) 
	{
		Object x = c.evaluate (expr);
		c.render(x);
	}

}
