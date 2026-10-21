# Contributing

Development takes place at [SonarQubeCommunity/sonar-ruby](https://github.com/SonarQubeCommunity/sonar-ruby).
Use the [GitHub issue tracker](https://github.com/SonarQubeCommunity/sonar-ruby/issues) to report bugs,
request features, or discuss substantial changes with the community maintainers before implementing them.
For a bug report, include the plugin version, SonarQube version, expected and actual behavior, and a minimal reproducer.
Report security vulnerabilities privately as described in [SECURITY.md](SECURITY.md).

## Making a change

1. Fork the repository and create a branch from `master`.
2. Follow the [build instructions](README.md#building) to initialize the shared build logic and set up a JDK.
3. Keep the change focused and add regression coverage for fixes or new behavior.
4. Run `./gradlew build` to build the plugin, run unit tests, and check license headers.
   For changes that affect analysis or SonarQube integration, also follow the [integration test instructions](README.md#integration-tests).
5. Open a pull request against `SonarQubeCommunity/sonar-ruby`'s `master` branch.
   Describe the problem, explain the change, link any related GitHub issue, and list the validation performed.

For rule descriptions and metadata, follow [the maintenance instructions](README.md#rule-descriptions-and-metadata).
For parser upgrades, see [Updating parser](sonar-ruby-plugin/README.md#updating-parser).

## License and notices

Read [LICENSE.txt](LICENSE.txt) before contributing. The project currently uses the Sonar Source-Available
License Version 1 (SSALv1), which restricts use, including competing with SonarQube.
Contributions are distributed under the applicable project license.

Keep existing copyright and license notices, including the SonarSource notices on code developed by SonarSource.
SSALv1 section 3.3 requires retaining these notices and allows contributors to add appropriate notices of their own.
The repository's community ownership does not transfer copyright or relicense existing code.

Use [LICENSE_HEADER](LICENSE_HEADER) for new Java files. `./gradlew spotlessApply` applies Java headers and
updates the existing Ruby header years. Keep the same license notice in new Ruby source files, using Ruby comments.
Check headers manually for files that Spotless excludes, as described in [License headers](README.md#license-headers).
