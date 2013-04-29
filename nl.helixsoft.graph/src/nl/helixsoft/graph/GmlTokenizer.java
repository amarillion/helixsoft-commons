package nl.helixsoft.graph;

import java.io.IOException;
import java.io.Reader;

public class GmlTokenizer
{
	private class ReaderWrapper
	{
		private Reader stream;
		
		ReaderWrapper (Reader s)
		{
			stream = s;
		}
		
		private boolean pushedBack = false;
		private int next;
		
		int row = 0;
		int col = 0;
		
		public int read() throws IOException
		{
			if (pushedBack)
			{
				pushedBack = false;
				return next;
			}
			else
			{
				next = stream.read();
				col ++;
				if (next == '\n') { row++; col = 0; }
				return next;
			}			
		}
		
		public void pushback()
		{
			if (pushedBack == true)
			{
				throw new IllegalStateException("Invalid pushback call");
			}
			pushedBack = true;
		}
	}
	
	public enum TokenType
	{
		EOF,
		WHITESPACE,
		INTEGER_LITERAL,
		DOUBLE_LITERAL,
		SIGN, 
		STRING_LITERAL, 
		RBRACKET, 
		LBRACKET, 
		KEYWORD,
		;
	}
	
	private final ReaderWrapper stream;
	
	GmlTokenizer (Reader stream)
	{
		this.stream = new ReaderWrapper(stream);
	}
	
	public class Token
	{
		private TokenType type;
		private double valueDouble;
		private String strValue;
		private int valueInt;
		
		Token (TokenType type)
		{
			this.type = type;
		}

		Token (TokenType type, double value)
		{
			this.type = type;
			this.valueDouble = value;
		}

		Token (TokenType type, int value)
		{
			this.type = type;
			this.valueInt = value;
		}

		Token (TokenType type, String strValue)
		{
			this.type = type;
			this.strValue = strValue;
		}
		
		public String toString()
		{
			String result = type.name();
			switch (type)
			{
			case INTEGER_LITERAL:
				result += " " + valueInt;
				break;
			case DOUBLE_LITERAL:
				result += " " + valueDouble;
				break;
			default:
				if (strValue != null)  result += " \"" + strValue + "\"";
				break;
			}
			return result;
		}

		public TokenType getType()
		{
			return type;
		}

		public String getStringValue()
		{
			return strValue;
		}

		public double getDoubleValue()
		{
			return valueDouble;
		}

		public int getIntValue()
		{
			return valueInt;
		}
	}
	
	private Token nextToken = null;
	
	public Token getLookAhead() throws IOException
	{
		nextToken = getToken();
		return nextToken;
	}
	
	private String readDigits() throws IOException
	{
		int ch = stream.read();
		String result = "";
		while ((ch >= '0' && ch <= '9'))
		{
			result += (char)ch;
			ch = stream.read();
		}
		stream.pushback();
		return result;
	}
	
	private String readFractionalPart() throws IOException
	{
		int ch = stream.read();
		String result = "";
		if (ch == '.')
		{
			result += (char)ch;
			result += readDigits();
			ch = stream.read();
		}
		if (ch == 'E')
		{
			result += (char)ch;
			ch = stream.read();
			if (ch == '+' || ch == '-')
			{
				result += (char)ch;
				ch = stream.read();
			}
			stream.pushback();
			result += readDigits();
		}
		return result;
	}
	
	private Token createDoubleToken(String value)
	{
		Token result;
		try
		{
			result = new Token(TokenType.DOUBLE_LITERAL, Double.parseDouble(value));
		}
		catch (NumberFormatException e)
		{
			throw new IllegalStateException ("Invalid floating point value '" + value + "'");
		}
		return result;
	}
	
	Token getToken() throws IOException
	{		
		
		Token token = null;
		if (nextToken != null)
		{
			token = nextToken;
			nextToken = null;
			return token;
		}

		int ch = stream.read();

		// read token
		switch (ch)
		{
		case -1:
			token = new Token (TokenType.EOF);			
			break;
		case ' ': case '\t': case '\n' : case '\r':
			{
				do 
				{
					ch = stream.read();
				} while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
				stream.pushback();
				token = new Token (TokenType.WHITESPACE);
			}
			break;
		case '.':
			{
				stream.pushback();
				String result = readFractionalPart();
				token = createDoubleToken(result);
			}
			break;
		case '+': case '-':
		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
		{
			String result = "";
			if (ch != '+') result += (char)ch; // don't add + in front, java no likes.
			
			result += readDigits();
			ch = stream.read();
			stream.pushback();
			
			if (ch == '.' || ch == 'E')
			{
				result += readFractionalPart();
				token = createDoubleToken(result);				
			}
			else
			{
				try
				{
					token = new Token (TokenType.INTEGER_LITERAL, Integer.parseInt(result));
				}
				catch (NumberFormatException e)
				{
					throw new IllegalStateException ("Invalid integer '" + result + "'");
				}
			}
		}
		break;
		case '[':
			token = new Token(TokenType.LBRACKET);
			break;
		case ']':
			token = new Token(TokenType.RBRACKET);
			break;
		case '"': {
			ch = stream.read();
			String value = "";
			while (ch != '"')
			{
				if (ch == -1 || ch == '\n' || ch == '\r') throw new IllegalStateException("Found unterminated string literal");
				value += (char)ch;
				ch = stream.read();
			}
			token = new Token(TokenType.STRING_LITERAL, value);
		} break;
		case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G':
		case 'H': case 'I': case 'J': case 'K': case 'L': case 'M':
		case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T':
		case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z':
		case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g':
		case 'h': case 'i': case 'j': case 'k': case 'l': case 'm':
		case 'n': case 'o': case 'p': case 'q': case 'r': case 's': case 't':
		case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
		case '_':
			String result = "" + (char)ch;
			ch = stream.read();
			while ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_')
			{
				result += (char)ch;
				ch = stream.read();
			}
			stream.pushback();
			token = new Token (TokenType.KEYWORD, result);
			break;
		default:
			throw new IllegalStateException("Unknown character " + ch + " '" + (char)ch + "'" + " in lineNo " + stream.row);
		}
		//~ System.out.print (token.type + ", ");
		return token;
	}

	public String getPosition()
	{
		return "line: " + stream.row + ", col: " + stream.col;
	}
}