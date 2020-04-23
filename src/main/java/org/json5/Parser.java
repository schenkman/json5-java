package org.json5;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.json5.Lexer.State;
import org.json5.Lexer.Token;
import org.json5.Lexer.TokenType;

public class Parser {

  private State currentState = State.DEFAULT;

  enum State {
    DEFAULT,
    OBJECT_KEY,
    OBJECT_VALUE,
    ARRAY
  }

  public String parse(String json5) {
    Lexer lexer = new Lexer(
        new InputStreamReader(new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8))));

    Token token = new Token(TokenType.TokenType_Unset);

    while (true) {
      switch (currentState) {
        case DEFAULT:
          token = lexer.getNextToken(Lexer.State.DEFAULT);
        case OBJECT_KEY:
          token = lexer.getNextToken(Lexer.State.OBJECT_KEY);
      }

      switch (token.tokenType) {
        case PUNCTUATION_OPENOBJECT:
          currentState = State.OBJECT_KEY;
      }
    }

//    return "";
  }

}
