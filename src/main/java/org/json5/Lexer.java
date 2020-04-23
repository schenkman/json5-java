package org.json5;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class Lexer {

  private final InputStreamReader reader;

  private State currentState = State.DEFAULT;

  private int currentLine = 0;
  private int currentColumn = 0;

  public Lexer(InputStreamReader reader) {
    this.reader = reader;
  }

  public Token getJson5Value() {
    TokenBuilder tokenBuilder = new TokenBuilder();
    //Value can be one of:
    // null
    // true
    // false
    // "string" or 'string'
    // {} object
    // [] array

    while(true) {
      int val = read();
      if (val == -1) {
        return new Token(TokenType.EOF);
      }
      char c = (char) val;
      trackPosition(val);
      tokenBuilder.addChar(c);

      TokenType tokenType = TokenType.TokenType_Unset;

      switch (c) {
        case '{':
          tokenType = TokenType.PUNCTUATION_OPENOBJECT;
        case '[':
          tokenType = TokenType.PUNCTUATION_OPENARRAY;
        case 'n':
          //May be the start of 'null'
        case 't':
          //May be the start of 'true'
        case 'f':
          //May be the start of 'false'
        case '\'':
          tokenType = TokenType.PUNCTUATION_STRING_SINGLE_QUOTE;
        case '"':
          tokenType = TokenType.PUNCTUATION_STRING_DOUBLE_QUOTE;

      }
    }
  }

  public Token getObjectKey() {
    currentState = State.OBJECT_KEY;
    TokenBuilder tokenBuilder = new TokenBuilder();

    while(true) {
      int val = read();
      if (val == -1) {
        return new Token(TokenType.EOF);
      }
      char c = (char) val;
      trackPosition(val);
      tokenBuilder.addChar(c);

      TokenType tokenType = TokenType.TokenType_Unset;

      switch (currentState) {
        case OBJECT_KEY:
          tokenType = handleObjectKey(c);
          break;
        case STRING_SINGLE_QUOTE_START:
          tokenType = handleStringSingleQuote(c);
          break;
        case STRING_DOUBLE_QUOTE_START:
          tokenType = handleStringDoubleQuote(c);
          break;
        default:
          //Anything else is an error
      }

      switch(tokenType) {
        case VALUE_STRING:
          tokenBuilder.removeLastChar();
          return tokenBuilder.toToken(tokenType);
        case PUNCTUATION_STRING_SINGLE_QUOTE:
          tokenBuilder.removeLastChar();
          currentState = State.STRING_SINGLE_QUOTE_START;
          break;
        case PUNCTUATION_STRING_DOUBLE_QUOTE:
          tokenBuilder.removeLastChar();
          currentState = State.STRING_DOUBLE_QUOTE_START;
          break;
        case CONTINUATION:
      }

    }
  }



  public Token getNextToken(State state) {
    TokenBuilder tokenBuilder = new TokenBuilder();

    while(true) {
      int val = read();
      if (val == -1) {
        return new Token(TokenType.EOF);
      }
      char c = (char) val;
      trackPosition(val);
      tokenBuilder.addChar(c);

      TokenType tokenType = TokenType.TokenType_Unset;

      switch (state) {
        case DEFAULT:
          tokenType = handleDefault(c);
        case OBJECT_KEY:
          tokenType = handleObjectKey(c);
        case OBJECT_VALUE:
          tokenType = handleObjectValue(c);
      }
    }


  }

  public Token getNextToken() {
    TokenBuilder tokenBuilder = new TokenBuilder();

    while(true) {
      int val = read();
      if (val == -1) {
        return new Token(TokenType.EOF);
      }
      char c = (char) val;
      trackPosition(val);
      tokenBuilder.addChar(c);

      TokenType tokenType = TokenType.TokenType_Unset;

      switch (currentState) {
        case DEFAULT:
          tokenType = handleDefault(c);
          break;
        case COMMENT_START:
          tokenType = handleCommentStart(c);
          break;
        case COMMENT_SINGLELINE:
          tokenType = handleCommentSingleLine(c);
          break;
        case COMMENT_MULTILINE:
          tokenType = handleCommentMultiLine(c);
          break;
        case COMMENT_MULTILINE_MAYBE_CLOSE:
          tokenType = handleCommentMultiLineMaybeClose(c);
          break;
        case STRING_SINGLE_QUOTE_START:
          tokenType = handleStringSingleQuote(c);
          break;
        case STRING_DOUBLE_QUOTE_START:
          tokenType = handleStringDoubleQuote(c);
          break;
//        case OBJECT_KEY:
//          tokenType = handleObjectKey(c);
//          break;
//        case OBJECT_VALUE:
//          tokenType = handleObjectValue(c);
//          break;
      }

      switch (tokenType) {
        case COMMENT_START:
          currentState = State.COMMENT_START;
          break;
        case COMMENT_SINGLELINE:
          currentState = State.COMMENT_SINGLELINE;
          break;
        case COMMENT_MULTILINE:
          currentState = State.COMMENT_MULTILINE;
          break;
        case COMMENT_END:
          tokenBuilder.resetValue();
          currentState = State.DEFAULT;
        case WHITESPACE_CONTINUATION:
          tokenBuilder.removeLastChar();
          break;
        case COMMENT_MULTILINE_MAYBE_CLOSE:
          currentState = State.COMMENT_MULTILINE_MAYBE_CLOSE;
          break;
        case TokenType_Unset:
          break;
        case INVALID:
        case UNKNOWN:
          currentState = State.ERROR;
          break;
        case PUNCTUATION_STRING_SINGLE_QUOTE:
          tokenBuilder.removeLastChar();
          currentState = State.STRING_SINGLE_QUOTE_START;
          break;
        case PUNCTUATION_STRING_DOUBLE_QUOTE:
          tokenBuilder.removeLastChar();
          currentState = State.STRING_DOUBLE_QUOTE_START;
          break;
        case CONTINUATION:
          break;
        case VALUE_STRING:
          currentState = State.DEFAULT;
          tokenBuilder.removeLastChar();
          return tokenBuilder.toToken(tokenType);
        case PUNCTUATION_NAME_SEPARATOR:
        case PUNCTUATION_OPENOBJECT:
        case PUNCTUATION_CLOSEOBJECT:
        case PUNCTUATION_OPENARRAY:
        case PUNCTUATION_CLOSEARRAY:
        case EOF:
          return tokenBuilder.toToken(tokenType);

      }
    }
  }

  private TokenType handleDefault(char c) {
    switch (c) {
      case '\t':
      case '\b':
      case '\n':
      case '\r':
      case '\f':
      case '\u00A0':
      case '\uFEFF':
      case '\u2028':
      case '\u2029':
      case ' ':
        return TokenType.WHITESPACE_CONTINUATION;

      case '/':
        return TokenType.COMMENT_START;
      case '{':
        return TokenType.PUNCTUATION_OPENOBJECT;
      case '}':
        return TokenType.PUNCTUATION_CLOSEOBJECT;
      case '[':
        return TokenType.PUNCTUATION_OPENARRAY;
      case ']':
        return TokenType.PUNCTUATION_CLOSEARRAY;
      case '\'':
        return TokenType.PUNCTUATION_STRING_SINGLE_QUOTE;
      case '"':
        return TokenType.PUNCTUATION_STRING_DOUBLE_QUOTE;
      case ':':
        return TokenType.PUNCTUATION_NAME_SEPARATOR;
      default:
        return TokenType.UNKNOWN;

    }
  }

  private TokenType handleCommentStart(char c) {
    switch (c) {
      case '/':
        return TokenType.COMMENT_SINGLELINE;
      case '*':
        return TokenType.COMMENT_MULTILINE;
      default:
        return TokenType.INVALID;
    }
  }

  private TokenType handleCommentSingleLine(char c) {
    if (c == '\n') {
      return TokenType.COMMENT_END;
    } else {
      return TokenType.WHITESPACE_CONTINUATION;
    }
  }

  private TokenType handleCommentMultiLine(char c) {
    if (c == '*') {
      return TokenType.COMMENT_MULTILINE_MAYBE_CLOSE;
    } else {
      return TokenType.WHITESPACE_CONTINUATION;
    }
  }

  private TokenType handleCommentMultiLineMaybeClose(char c) {
    if (c == '/') {
      return TokenType.COMMENT_END;
    } else if (c == '*') {
      return TokenType.COMMENT_MULTILINE_MAYBE_CLOSE;
    } else {
      return TokenType.COMMENT_MULTILINE;
    }
  }

  private TokenType handleStringSingleQuote(char c) {
    if (c == '\'') {
      return TokenType.VALUE_STRING;
    } else {
      return TokenType.CONTINUATION;
    }
  }

  private TokenType handleStringDoubleQuote(char c) {
    if (c == '"') {
      return TokenType.VALUE_STRING;
    } else {
      return TokenType.CONTINUATION;
    }
  }

  private TokenType handleObjectKey(char c) {
    if (c == '"') {
      return TokenType.PUNCTUATION_STRING_DOUBLE_QUOTE;
    } else if (c == '\'') {
      return TokenType.PUNCTUATION_STRING_SINGLE_QUOTE;
    } else {
      return TokenType.CONTINUATION;
    }
  }

  private TokenType handleObjectValue(char c) {
    if (c == '"') {
      return TokenType.PUNCTUATION_STRING_DOUBLE_QUOTE;
    } else if (c == '\'') {
      return TokenType.PUNCTUATION_STRING_SINGLE_QUOTE;
    } else if (c == '\n') {
      return TokenType.VALUE_STRING;
    } else {
      return TokenType.CONTINUATION;
    }
  }

  private int read() {
    try {
      return reader.read();
    } catch (IOException e) {
      throw new RuntimeException("Things went bad reading the file");
    }
  }

  private void trackPosition(int val) {
    char ch = (char) val;
    if (ch == '\n') {
      currentLine++;
      currentColumn = 0;
    } else {
      currentColumn++;
    }
  }

  enum State {
    State_Unset,
    ERROR,
    DEFAULT,
    COMMENT_START,
    COMMENT_SINGLELINE,
    COMMENT_MULTILINE,
    COMMENT_MULTILINE_MAYBE_CLOSE,
    STRING_SINGLE_QUOTE_START,
    STRING_DOUBLE_QUOTE_START,
    OBJECT_KEY,
    OBJECT_VALUE,
    ARRAY_ELEMENT,
  }

  enum TokenType {
    TokenType_Unset,
    INVALID,
    UNKNOWN,
    CONTINUATION,
    WHITESPACE_CONTINUATION,
    PUNCTUATION_OPENOBJECT,
    PUNCTUATION_CLOSEOBJECT,
    OBJECT_KEY,
    VALUE_STRING,
    VALUE_NUMBER,
    PUNCTUATION_OPENARRAY,
    PUNCTUATION_CLOSEARRAY,
    PUNCTUATION_STRING_SINGLE_QUOTE,
    PUNCTUATION_STRING_DOUBLE_QUOTE,
    PUNCTUATION_NAME_SEPARATOR,
    BOOLEAN_TRUE,
    BOOLEAN_FALSE,
    NULL,
    COMMENT_START,
    COMMENT_SINGLELINE,
    COMMENT_MULTILINE,
    COMMENT_MULTILINE_MAYBE_CLOSE,
    COMMENT_END,
    EOF,
  }

  static class TokenBuilder {
    private StringBuilder value = new StringBuilder();

    public void addChar(char c) {
      value.append(c);
    }

    public void removeLastChar() {
      if (value.length() > 0) {
        value.deleteCharAt(value.length() - 1);
      }
    }

    public Token toToken(TokenType tokenType) {
      return new Token(tokenType, value.toString());
    }

    public void resetValue() {
      value.setLength(0);
    }
  }

  static class Token {
    public final TokenType tokenType;
    public final String value;

//    public static final Token WHITESPACE_CONTINUATION = new Token(TokenType.WHITESPACE_CONTINUATION);

    public Token(TokenType tokenType) {
      this.tokenType = tokenType;
      this.value = null;
    }

    public Token(TokenType tokenType, String value) {
      this.tokenType = tokenType;
      this.value = value;
    }

    public Optional<String> getValue() {
      return Optional.ofNullable(value);
    }
  }

}
