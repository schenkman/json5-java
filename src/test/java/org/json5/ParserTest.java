package org.json5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json5.Lexer.Token;
import org.json5.Lexer.TokenType;
import org.junit.jupiter.api.Test;

public class ParserTest {

  @Test
  public void parse_parsesEmptyObject() {
    String json5 = "  {  }  ";

    Parser parser = new Parser();

    String json = parser.parse(json5);

    assertEquals("{}", json);
  }

  @Test
  public void parse_throwsOnNonClosedObject() {
    String json5 = "  {    ";

    Parser parser = new Parser();

    String json = "";
    try {
      json = parser.parse(json5);
      fail("Should have thrown");
    } catch (IllegalStateException e) {

    }
  }

  @Test
  public void parse_parsesEmptyArray() {
    String json5 = "  [  ]  ";

    Parser parser = new Parser();

    String json = parser.parse(json5);

    assertEquals("[]", json);
  }

  @Test
  public void parse_throwsOnNonClosedArray() {
    String json5 = "  [    ";

    Parser parser = new Parser();

    String json = "";
    try {
      json = parser.parse(json5);
      fail("Should have thrown");
    } catch (IllegalStateException e) {
      System.out.println(e);
    }
  }

  @Test
  public void parse_throwsOnNonIncorrectlyClosedArray() {
    String json5 = "  [  }  ";

    Parser parser = new Parser();

    String json = "";
    try {
      json = parser.parse(json5);
      fail("Should have thrown");
    } catch (IllegalStateException e) {
      System.out.println(e);
    }
  }
}