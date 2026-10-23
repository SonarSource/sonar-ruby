# Contributing

## Reporting Issues

Report bugs and request features in [GitHub issues](https://github.com/SonarQubeCommunity/sonar-ruby/issues). Include the plugin version, SonarQube version, and a minimal Ruby code sample. Report security vulnerabilities as described in [SECURITY.md](SECURITY.md).

## Submitting Changes

1. Fork the repository and create a branch from `master`.
2. Follow the [build instructions](README.md#building). Use JDK 21 to match CI, and initialize the integration test sources with `git submodule update --init its/sources`.
3. Add or update tests that cover your change.
4. Run `./gradlew spotlessApply` to update formatting and license headers, then `./gradlew build -Pits --no-daemon --console=plain` to run unit and integration tests.
5. Open a pull request against `master` and describe what the change does and why. Link the related GitHub issue, if any.

The [Build](.github/workflows/build.yml) workflow runs unit and integration tests on every pull request.

## License

By contributing, you agree that your contributions are licensed under the [Sonar Source-Available License Version 1 (SSALv1)](LICENSE.txt), like the rest of the project.
