package org.json5;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import org.json5.Lexer.Token;
import org.json5.Lexer.TokenType;

public class Parser {

  private State currentState = State.DEFAULT;

  enum State {
    DEFAULT,
    OBJECT_KEY,
    OBJECT_VALUE,
    ARRAY,
    EOF
  }

  public String parse(String json5) {
    Lexer lexer = new Lexer(
        new InputStreamReader(new ByteArrayInputStream(json5.getBytes(StandardCharsets.UTF_8))));

    Token token = new Token(TokenType.TokenType_Unset);
    StringBuilder json = new StringBuilder();
    Stack<TokenType> stack = new Stack<>();

    while (currentState != State.EOF) {
      switch (currentState) {
        case DEFAULT:
          token = lexer.getNextToken();
          break;
        case EOF:
          break;
        default:
          throw new IllegalStateException("We shouldn't be here");
      }

      System.out.println("Token: " + token.tokenType + " - " + token.value);
      switch (token.tokenType) {
        case EOF:
          if (!stack.isEmpty()) {
            throw new IllegalStateException("Received EOF when there was more stuff expected");
          }
          currentState = State.EOF;
          break;
        case PUNCTUATION_OPENOBJECT:
          stack.push(token.tokenType);
          json.append(token.value);
          break;
        case PUNCTUATION_CLOSEOBJECT:
          TokenType pop = stack.pop();
          if (pop != TokenType.PUNCTUATION_OPENOBJECT) {
            throw new IllegalStateException("Received " + token.tokenType
                    + " at line " + lexer.getCurrentLine() + " column "
                    + lexer.getCurrentColumn());
          }
          currentState = State.DEFAULT;
          json.append(token.value);
          break;
        case PUNCTUATION_OPENARRAY:
          stack.push(token.tokenType);
          json.append(token.value);
          break;
        case PUNCTUATION_CLOSEARRAY:
          pop = stack.pop();
          if (pop != TokenType.PUNCTUATION_OPENARRAY) {
            throw new IllegalStateException("Received " + token.tokenType
                + " at line " + lexer.getCurrentLine() + " column "
                + lexer.getCurrentColumn());
          }
          currentState = State.DEFAULT;
          json.append(token.value);
          break;
      }
    }

    return json.toString();
  }

}
