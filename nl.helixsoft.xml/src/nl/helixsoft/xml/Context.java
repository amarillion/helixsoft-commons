package nl.helixsoft.xml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class Context 
{
	StringBuilder builder;
	private Object data;
	
	public String build()
	{
		return builder.toString();
	}
	
	public Context()
	{
		this(null);
	}
	
	public Context(Object data)
	{
		builder = new StringBuilder();
		this.data = data;
	}
	
	public Object evaluate (String expr) 
	{
		if (data == null) throw new NullPointerException();
		
		// if it is a map, just look up the appropriate key
		if (data instanceof Map)
		{
			return ((Map<?, ?>)data).get(expr);
		}
		
		// use reflection to get data.
		for (Method m : data.getClass().getMethods())
		{
			String n = m.getName();
			if (m.getParameterTypes().length > 0) continue;
			
			if (n.equalsIgnoreCase(expr) || n.equalsIgnoreCase("get" + expr))
			{
				Object result;
				try {
					result = m.invoke(data);
					return result;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		throw new IllegalArgumentException("Nothing found for template expr: " + expr);		
	}
	
	public void render (Object x)
	{
		if (x instanceof HtmlRenderable)
		{
			((HtmlRenderable)x).flush(this);
		}
		else if (x instanceof List)
		{
			for (Object o : (List<?>)x)
			{
				render (o);
			}
		}
		else
		{
			builder.append("" + x);
		}
	}
	
}
