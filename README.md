# To Do - Wed Apr 22
- Do some code cleanup
- Get the project on github


# To Do - Fri Apr 17
- Continue the refactor into more specialized Lexer methods that handle specific states
  - i.e. OBJECT_KEY (already done), OBJECT_VALUE (pending)
- Start moving states into the Parser object
  - Write tests around the parser's perspective

# To Do - Wed Apr 15
- Handle non-quoted strings
  - we ignore whitespace after the punctuation, read until newline (like yaml)
  - `handleUnquotedString(char c)`
- Handle numbers? Do we care? Probably not. Just treat like a string
- Handle multiple key/values or array members


