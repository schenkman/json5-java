package org.json5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json5.Lexer.Token;
import org.json5.Lexer.TokenType;
import org.junit.jupiter.api.Test;

public class LexerTest {

  @Test
  public void getObjectKey() {
    String json5 = "'a key' : 'a value'";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));

    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getObjectKey();

    assertEquals(TokenType.VALUE_STRING, token.tokenType);
    assertEquals("a key", token.getValue().get());

    json5 = "\"another key\" : 'another value'";
    inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    lexer = new Lexer(new InputStreamReader(inputStream));
    token = lexer.getObjectKey();

    assertEquals(TokenType.VALUE_STRING, token.tokenType);
    assertEquals("another key", token.getValue().get());

  }


  @Test
  public void lexerHandlesOpenAndCloseObject() {
    String json5 = "   {  }  ";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));

    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);
  }

  @Test
  public void lexerHandlesSingleLineComments() {
    String json5 = "//Comment at start\n{}";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);

    json5 = "{//Comment Within object\n}";
    inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    lexer = new Lexer(new InputStreamReader(inputStream));
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);

    json5 = "{}//Comment after object";
    inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    lexer = new Lexer(new InputStreamReader(inputStream));
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);
  }

  @Test
  public void lexerHandlesMultiLineComments() {
    String json5 = "/* Multiline comment at start\n with a newline */\n{}";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);

    json5 = "\n{/* Multiline comment inside\n with a newline */}";
    inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    lexer = new Lexer(new InputStreamReader(inputStream));
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);

    json5 = "{} /* Multiline comment at end\n with a newline */";
    inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    lexer = new Lexer(new InputStreamReader(inputStream));
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);

    json5 = "{} /* Multiline comment at end with no end of comment";
    inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    lexer = new Lexer(new InputStreamReader(inputStream));
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEOBJECT, token.tokenType);
    assertEquals("}", token.getValue().get());
    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);
  }

  @Test
  public void lexerHandlesOpenAndCloseArray() {
    String json5 = "   [  ]  ";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));

    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENARRAY, token.tokenType);
    assertEquals("[", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_CLOSEARRAY, token.tokenType);
    assertEquals("]", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);
  }

  @Test
  public void lexerHandlesSingleQuoteObjectKeyAndStringValue() {
    String json5 = "{ 'key' : 'value'";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.VALUE_STRING, token.tokenType);
    assertEquals("key", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_NAME_SEPARATOR, token.tokenType);
    assertEquals(":", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.VALUE_STRING, token.tokenType);
    assertEquals("value", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);
  }

//  @Test
//  public void lexerHandlesSingleQuoteObjectKeyAndUnquotedValue() {
//    String json5 = "{ 'number' : this is a value\n }";
//    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
//    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
//    Token token = lexer.getNextToken();
//    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
//    assertEquals("{", token.getValue().get());
//
//    token = lexer.getNextToken();
//    assertEquals(TokenType.VALUE_STRING, token.tokenType);
//    assertEquals("number", token.getValue().get());
//
//    token = lexer.getNextToken();
//    assertEquals(TokenType.PUNCTUATION_NAME_SEPARATOR, token.tokenType);
//    assertEquals(":", token.getValue().get());
//
//    token = lexer.getNextToken();
//    assertEquals(TokenType.VALUE_STRING, token.tokenType);
//    assertEquals("this is a value", token.getValue().get());
//
//    token = lexer.getNextToken();
//    assertEquals(TokenType.EOF, token.tokenType);
//  }


  @Test
  public void lexerHandlesDoubleQuoteObjectKey() {
    String json5 = "{ \"key\"";
    InputStream inputStream = new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8));
    Lexer lexer = new Lexer(new InputStreamReader(inputStream));
    Token token = lexer.getNextToken();
    assertEquals(TokenType.PUNCTUATION_OPENOBJECT, token.tokenType);
    assertEquals("{", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.VALUE_STRING, token.tokenType);
    assertEquals("key", token.getValue().get());

    token = lexer.getNextToken();
    assertEquals(TokenType.EOF, token.tokenType);
  }
}