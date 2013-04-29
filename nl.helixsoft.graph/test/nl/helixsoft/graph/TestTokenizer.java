package nl.helixsoft.graph;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import nl.helixsoft.graph.GmlTokenizer;
import nl.helixsoft.graph.GmlTokenizer.Token;
import nl.helixsoft.graph.GmlTokenizer.TokenType;

import junit.framework.TestCase;

public class TestTokenizer extends TestCase
{
	private void failHelper(String x)
	{
		Reader reader = new StringReader (x);
		GmlTokenizer tokenizer = new GmlTokenizer(reader);
		try
		{
			tokenizer.getToken();
			fail ("Expected an exception");
		}
		catch (IOException e)
		{
			fail("No IOException expected " + e.getMessage());
		}		
		catch (IllegalStateException e)
		{
			// success!
		}
	}
	
	private Token helper(String x, TokenType expected)
	{
		Reader reader = new StringReader (x);
		GmlTokenizer tokenizer = new GmlTokenizer(reader);
		Token result = null;
		try
		{
			result = tokenizer.getToken();
			assertEquals (expected, result.getType());
			assertEquals (TokenType.EOF, tokenizer.getToken().getType());
		}
		catch (IOException e)
		{
			fail("No exception expected " + e.getMessage());
		}
		return result;
	}
	
	public void testIntegers()
	{
		Token token;
		
		token = helper ("1", TokenType.INTEGER_LITERAL);
		assertEquals (1, token.getIntValue());

		token = helper ("-2", TokenType.INTEGER_LITERAL);
		assertEquals (-2, token.getIntValue());
		
		token = helper ("+3", TokenType.INTEGER_LITERAL);
		assertEquals (3, token.getIntValue());
		
		token = helper ("345", TokenType.INTEGER_LITERAL);
		assertEquals (345, token.getIntValue());
	}

	public void testDoubles()
	{
		Token token;
		
		token = helper (".1", TokenType.DOUBLE_LITERAL);
		assertEquals (0.1, token.getDoubleValue(), 0.0001);			

		token = helper ("-.2", TokenType.DOUBLE_LITERAL);
		assertEquals (-0.2, token.getDoubleValue(), 0.0001);
		
		token = helper ("+.3", TokenType.DOUBLE_LITERAL);
		assertEquals (0.3, token.getDoubleValue(), 0.0001);
		
		token = helper ("1E2", TokenType.DOUBLE_LITERAL);
		assertEquals (100, token.getDoubleValue(), 0.0001);
		
		token = helper ("1E-2", TokenType.DOUBLE_LITERAL);
		assertEquals (0.01, token.getDoubleValue(), 0.0001);			

		token = helper ("1E10", TokenType.DOUBLE_LITERAL);
		assertEquals (1e10, token.getDoubleValue(), 1);			

		token = helper ("1.2E1", TokenType.DOUBLE_LITERAL);
		assertEquals (12, token.getDoubleValue(), 0.0001);			

		token = helper ("1.2E-1", TokenType.DOUBLE_LITERAL);
		assertEquals (0.12, token.getDoubleValue(), 0.0001);			

		token = helper ("-0.55", TokenType.DOUBLE_LITERAL);
		assertEquals (-0.55, token.getDoubleValue(), 0.0001);			

		token = helper ("+0.56", TokenType.DOUBLE_LITERAL);
		assertEquals (0.56, token.getDoubleValue(), 0.0001);			

		token = helper ("7.", TokenType.DOUBLE_LITERAL);
		assertEquals (7, token.getDoubleValue(), 0.0001);			
	}

	public void testFail()
	{
		// non-terminated string
		failHelper ("\"abc");
		
		// string must be closed on the same line
		failHelper ("\"abc\n");
		
		// unknown special character
		failHelper ("<");
		
		// unknown special character
		failHelper ("&");
	}
}

