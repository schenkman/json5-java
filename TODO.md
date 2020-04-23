# To-Do List

This is a pile of my thoughts of things that need to be done.

## To Do - Wed Ap 22
- Add a logging implementation, probably slf4j with logback
- Continue with Parser vs Lexer states

## To Do - Fri Apr 17
- Continue the refactor into more specialized Lexer methods that handle specific states
  - i.e. OBJECT_KEY (already done), OBJECT_VALUE (pending)
- Start moving states into the Parser object
  - Write tests around the parser's perspective

## To Do - Wed Apr 15
- Handle non-quoted strings
  - we ignore whitespace after the punctuation, read until newline (like yaml)
  - `handleUnquotedString(char c)`
- Handle numbers? Do we care? Probably not. Just treat like a string
- Handle multiple key/values or array members
