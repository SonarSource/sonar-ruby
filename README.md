# SonarQube analyzer for Ruby

[![Build](https://github.com/SonarQubeCommunity/sonar-ruby/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/SonarQubeCommunity/sonar-ruby/actions/workflows/build.yml)

This repository contains the SonarQube analyzer for Ruby.

## Community Maintenance

The analyzer was developed by SonarSource and is now maintained by the community in the [SonarQubeCommunity](https://github.com/SonarQubeCommunity) organization. It uses the [SLang framework](https://github.com/SonarQubeCommunity/slang).

This README documents development of the analyzer.

We use [whitequark parser](https://github.com/whitequark/parser) to parse the Ruby language by embedding it using JRuby runtime.

* AST documentation for the parser can be found [here](https://github.com/whitequark/parser/blob/master/doc/AST_FORMAT.md)
* We use simple [Ruby script](sonar-ruby-plugin/src/main/resources/whitequark_parser_init.rb) to call the parser and invoke our [visitor](sonar-ruby-plugin/src/main/java/org/sonarsource/ruby/converter/RubyVisitor.java) written in Java

## Rule metadata

Native Ruby rule metadata is stored in [rules/ruby](sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/ruby). Rule descriptions are stored as HTML files. Modify the corresponding file to update the description in SonarQube.
To refresh RuboCop rule metadata, follow the [RuboCop generation instructions](utils/generate-rubocop-rules/README.md).

## Feedback and Contributions

Report bugs and request features in [GitHub issues](https://github.com/SonarQubeCommunity/sonar-ruby/issues). Include the plugin version, SonarQube version, and a minimal Ruby code sample. Contributions are welcome: see [CONTRIBUTING.md](CONTRIBUTING.md). For security vulnerabilities, follow [SECURITY.md](SECURITY.md).

## Building

### Build

Use JDK 17 or newer.

Build and run Unit Tests:

    ./gradlew build

Dependencies are downloaded from Maven Central, the Gradle Plugin Portal, and RubyGems.
No SonarSource repository credentials are required.

## Integration Tests

By default, Integration Tests (ITs) are skipped during builds.

If you want to run them, you need first to retrieve the related projects which are used as input:

    git submodule update --init its/sources

Then build and run the Integration Tests using the `its` property:

    ./gradlew build -Pits --info --no-daemon

Run the integration tests with a specific version of the SonarQube Community Build:
  
    ./gradlew build -Pits --info --no-daemon -Dsonar.runtimeVersion=26.10.0.133078

You can also build and run only Ruling Tests using the `ruling` property:

    ./gradlew build -Pruling --info --no-daemon


## Releasing

1. Create and publish a GitHub release with a tag such as `v1.26.0` pointing to the commit to release.
2. The [Release](.github/workflows/release.yml) workflow builds the project and runs unit and integration tests using the version from the tag. It attaches the installable `sonar-ruby-plugin-<version>.jar` and its SHA-256 checksum to the release. Wait for the workflow to succeed before announcing the release.
3. Set `version` in `gradle.properties` to the next development version, such as `1.26.1-SNAPSHOT`.

## Installing

Download the plugin JAR from [GitHub releases](https://github.com/SonarQubeCommunity/sonar-ruby/releases), copy it into your SonarQube server's `extensions/plugins` directory, and restart the server.

## License headers

License headers are automatically updated by the spotless plugin but only for Java files.
Furthermore, there are files such as `package-info.java` and `module-info.java` that spotless ignores.  For those files use a manual script like below to update the license. E.g. on Mac:

    `find . -type f -name "*-info.java" -exec sed -i '' 's/2018-2025/2018-2026/' "{}" \;`


## License

Copyright 2018-2026 SonarSource.

SonarQube analyzers released after November 29, 2024, including patch fixes for prior versions,
are published under the [Sonar Source-Available License Version 1 (SSALv1)](LICENSE.txt).

See individual files for details that specify the license applicable to each file.
Files subject to the SSALv1 will be noted in their headers.
