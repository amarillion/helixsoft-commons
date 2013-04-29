package nl.helixsoft.graph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import nl.helixsoft.graph.GmlTokenizer.Token;
import nl.helixsoft.graph.GmlTokenizer.TokenType;

public class GmlParser
{
	private final Emitter emitter;
	
	private final GmlTokenizer tokenizer;
	
	private GmlParser (Reader fis, Emitter emitter) throws IOException
	{
		tokenizer = new GmlTokenizer(fis);
		this.emitter = emitter;
		eatList();
		getExpectedToken(TokenType.EOF);
	}
	
	private Token getExpectedToken (TokenType type) throws IOException
	{
		Token token = tokenizer.getToken();
		if (token.getType() != type) throw new IllegalStateException("Expected " + type + ", but found " + token.getType() + " in " + tokenizer.getPosition());
		return token;
	}
	
	private void eatWhitespace() throws IOException
	{
		Token token = tokenizer.getLookAhead();
		while (token.getType() == TokenType.WHITESPACE)
		{
			tokenizer.getToken(); // consume token
			token = tokenizer.getLookAhead();
		}
	}

	private void eatValue(String key) throws IOException
	{
		Token token = tokenizer.getLookAhead();
		switch (token.getType())
		{
		case LBRACKET:
			emitter.startList(key);
			tokenizer.getToken();
			eatList();
			eatWhitespace();
			getExpectedToken(TokenType.RBRACKET);
			emitter.closeList();
			break;
		case INTEGER_LITERAL:
			token = tokenizer.getToken();
			emitter.intLiteral (key, token.getIntValue()); 
			break;
		case DOUBLE_LITERAL:
			tokenizer.getToken();
			emitter.doubleLiteral (key, token.getDoubleValue()); 
			break;
		case STRING_LITERAL:
			token = tokenizer.getToken();
			emitter.stringLiteral (key, token.getStringValue()); 
			break;
		default:
			throw new IllegalStateException("Expected LBRACKET, NUMBER_LITERAL or STRING_LITERAL, found " + token.getType());
		}
	}
		
	private void eatList() throws IOException
	{
		eatWhitespace();
		Token token = tokenizer.getLookAhead();
		while (token.getType() == TokenType.KEYWORD)
		{
			String key = tokenizer.getToken().getStringValue();
			getExpectedToken(TokenType.WHITESPACE);
			eatValue(key);
			eatWhitespace();
			token = tokenizer.getLookAhead();
		}
	}

	public static void parse(File f, Emitter emitter) throws IOException
	{
		new GmlParser (new FileReader(f), emitter);
	}

	public static void parse(Reader r, Emitter emitter) throws IOException
	{
		new GmlParser (r, emitter);
	}
}
