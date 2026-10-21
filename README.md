# SonarQube analyzer for Ruby

[![Build Status](https://github.com/SonarQubeCommunity/sonar-ruby/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/SonarQubeCommunity/sonar-ruby/actions/workflows/build.yml)

This repository contains the community-maintained SonarQube analyzer for Ruby, originally developed by SonarSource.
Its home is [SonarQubeCommunity/sonar-ruby](https://github.com/SonarQubeCommunity/sonar-ruby).

This is a developer documentation. If you want to analyze source code in SonarQube read the [analysis of Ruby documentation](https://docs.sonarqube.org/latest/analysis/languages/ruby/).

We use [whitequark parser](https://github.com/whitequark/parser) to parse the Ruby language by embedding it using JRuby runtime.

* AST documentation for the parser can be found [here](https://github.com/whitequark/parser/blob/master/doc/AST_FORMAT.md)
* We use simple [Ruby script](sonar-ruby-plugin/src/main/resources/whitequark_parser_init.rb) to call the parser and invoke our [visitor](sonar-ruby-plugin/src/main/java/org/sonarsource/ruby/converter/RubyVisitor.java) written in Java

## Have questions or feedback?

Report bugs and request features in the [GitHub issue tracker](https://github.com/SonarQubeCommunity/sonar-ruby/issues).
Include the plugin version, SonarQube version, and a minimal example when reporting a problem.
For security vulnerabilities, follow [SECURITY.md](SECURITY.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the contribution workflow, validation, and license requirements.

## Building

### Build

Use JDK 17 or newer.

Build and run Unit Tests:

    ./gradlew build

Dependencies are downloaded from Maven Central, the Gradle Plugin Portal, and RubyGems.
No SonarSource repository credentials are required.

## Releases

Releases are distributed as plugin JARs attached to [GitHub Releases](https://github.com/SonarQubeCommunity/sonar-ruby/releases).
The Maven group ID remains `org.sonarsource.slang` for metadata continuity; releases do not publish to Repox or Artifactory.

Maintainers can prepare a release as follows:

1. Set `version` in `gradle.properties` to the release version without `-SNAPSHOT`, for example `1.26.0`.
2. Merge the version change, then push a tag with the same version, for example `git tag 1.26.0` and `git push origin 1.26.0`.
3. The [release workflow](.github/workflows/release.yml) validates the tag, builds the plugin, runs unit tests,
   and creates a draft GitHub release with `sonar-ruby-plugin-<version>-all.jar` attached.
   It uses public dependencies and the repository's `GITHUB_TOKEN` with `contents: write`; no publishing secrets are needed.
4. Review the release notes and JAR before publishing the draft release. Agree on signing or checksum requirements
   with the community maintainers before the first release; the workflow does not currently produce either.
5. Update the Marketplace metadata in [sonar-update-center-properties](https://github.com/SonarSource/sonar-update-center-properties)
   with the release date, download URL, and tested compatibility ranges. The download URL follows
   `https://github.com/SonarQubeCommunity/sonar-ruby/releases/download/<version>/sonar-ruby-plugin-<version>-all.jar`.
   Community Build compatibility must start at 26.11 or later, since earlier versions bundle a Ruby analyzer.
   Only list Server versions that do not already provide an analyzer using the `ruby` language key.
6. Set `version` to the next development version ending in `-SNAPSHOT`.

## Integration Tests

By default, Integration Tests (ITs) are skipped during builds.
The tests build the Ruby plugin locally and download SonarQube Community Build, SonarScanner,
and the ruling-test plugin from Maven Central. No license or SonarSource credentials are required.
`sonar.runtimeVersion` defaults to the latest Community Build release; set it to a published
Community Build version to test another release, for example `-Dsonar.runtimeVersion=26.10.0.133078`.

If you want to run them, you need first to retrieve the related projects which are used as input:

    git submodule update --init its/sources

Then build and run the Integration Tests using the `its` property:

    ./gradlew build -Pits --info --no-daemon

You can also build and run only Ruling Tests using the `ruling` property:

    ./gradlew build -Pruling --info --no-daemon

## Rule descriptions and metadata

The committed files are the source of truth. Edit rule descriptions and metadata directly in:

* [`sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/ruby`](sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/ruby): each Ruby rule has an HTML description and JSON metadata.
* [`sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/rubocop/rules.json`](sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/rubocop/rules.json): RuboCop external-rule metadata, including descriptions.

These files are maintained by hand and are not regenerated from RSPEC or rule-api.
For RuboCop updates, follow the [manual refresh instructions](sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/rubocop/README.md).


## License headers

License headers are automatically updated by the spotless plugin but only for Java files.
Furthermore, there are files such as `package-info.java` and `module-info.java` that spotless ignores.  For those files use a manual script like below to update the license. E.g. on Mac:

    `find . -type f -name "*-info.java" -exec sed -i '' 's/2018-2025/2018-2026/' "{}" \;`


## License

Copyright 2018-2026 SonarSource.

This project uses the [Sonar Source-Available License Version 1 (SSALv1)](LICENSE.txt).
The move to SonarQubeCommunity does not change the license or the existing copyright notices.
SSALv1 restricts use, including competing with SonarQube; review its terms before using or contributing to the project.

See individual files for details that specify the license applicable to each file.
Files subject to the SSALv1 will be noted in their headers.
