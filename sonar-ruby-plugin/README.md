## Updating parser

To upgrade the Ruby parser, update `rubygemsParserVersion` in [build.gradle](build.gradle),
the parser library path in [RubyConverter.java](src/main/java/org/sonarsource/ruby/converter/RubyConverter.java),
and the parser import in [whitequark_parser_init.rb](src/main/resources/whitequark_parser_init.rb).

Follow the repository's [contribution instructions](../CONTRIBUTING.md) and run both unit and integration tests
to check that the upgraded parser still produces the expected analysis results.
