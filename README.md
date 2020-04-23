# json5-java

This is an implementation of the JSON5 specification for JVM-based languages.
You can find the JSON5 spec at https://spec.json5.org/ and the main website at [JSON5.org].

[JSON5.org]: https://json5.org/

This project is a work in progress, so don't expect the code to be clean, or even work. It's largely
an exercise in learning how to write a decent parser.

Work on this project will happen over Twitch streaming, which you can find at
https://twitch.tv/chrisdoescoding. Any questions, please feel free to send me a message.


## Currently in Progress

- Building the Lexer to return correct tokens and their values
- Moving logic into the Parser to handle the higher-level JSON5 grammar

There are plenty of things that will need to be cleaned up, I'm currently skipping things like
handling unquoted values or unquoted keys in objects. Those will come later.

## License

The license is an MIT license, see [LICENSE.md](./LICENSE.md) for details.

## Credits

Motivation for this project came from the JSON5 project as mentioned above. Credit goes to them
for all the work on writing a good spec and having a cool goal to have better JSON.