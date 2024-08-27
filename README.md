# sonar-ruby

[![Build Status](https://api.cirrus-ci.com/github/SonarSource/slang.svg?branch=master)](https://cirrus-ci.com/github/SonarSource/sonar-ruby)
[![Quality Gate Status](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=SonarSource_sonar-ruby&metric=alert_status&token=sqb_a78a1551add08b844fb442b01edcb5cc144ed1cb)](https://next.sonarqube.com/sonarqube/dashboard?id=SonarSource_sonar-ruby)
[![Coverage](https://next.sonarqube.com/sonarqube/api/project_badges/measure?project=SonarSource_sonar-ruby&metric=coverage&token=sqb_a78a1551add08b844fb442b01edcb5cc144ed1cb)](https://next.sonarqube.com/sonarqube/dashboard?id=SonarSource_sonar-ruby)

This is a developer documentation. If you want to analyze source code in SonarQube read the [analysis of Ruby documentation](https://docs.sonarqube.org/latest/analysis/languages/ruby/).

We use [whitequark parser](https://github.com/whitequark/parser) to parse the Ruby language by embedding it using JRuby runtime.

* AST documentation for the parser can be found [here](https://github.com/whitequark/parser/blob/master/doc/AST_FORMAT.md)
* We use simple [Ruby script](sonar-ruby-plugin/src/main/resources/whitequark_parser_init.rb) to call the parser and invoke our [visitor](sonar-ruby-plugin/src/main/java/org/sonarsource/ruby/converter/RubyVisitor.java) written in Java

## Have questions or feedback?

To provide feedback (request a feature, report a bug, etc.) use the [SonarQube Community Forum](https://community.sonarsource.com/). Please do not forget to specify the language, plugin version, and SonarQube version.

## Building

### Build
Build and run Unit Tests:

    ./gradlew build

## Integration Tests

By default, Integration Tests (ITs) are skipped during builds.
If you want to run them, you need first to retrieve the related projects which are used as input:

    git submodule update --init its/sources

Then build and run the Integration Tests using the `its` property:

    ./gradlew build -Pits --info --no-daemon

You can also build and run only Ruling Tests using the `ruling` property:

    ./gradlew build -Pruling --info --no-daemon


## License headers

License headers are automatically updated by the spotless plugin but only for Java files.
Furthermore, there are files such as `package-info.java` and `module-info.java` that spotless ignores. 
