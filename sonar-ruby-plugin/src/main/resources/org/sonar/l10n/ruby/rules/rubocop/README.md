# Maintaining RuboCop metadata

The committed [`rules.json`](rules.json) is the source of truth for RuboCop external-rule
metadata. Edit it directly. The former generator and its YAML snapshot have been removed
so that regeneration cannot overwrite reviewed metadata changes.

To refresh the metadata manually:

1. Choose a RuboCop release and record its version in the pull request. Install that
   version from RubyGems, for example `gem install --user-install rubocop --version 1.90.0`.
2. From an empty directory outside this repository, run
   `ruby -S rubocop _1.90.0_ --show-cops > rubocop.yml`, substituting the chosen version.
   This exports the public cop descriptions without loading a project's RuboCop configuration.
3. Compare the exported cop keys and descriptions with `rules.json`. Edit the JSON entries
   you intend to update, keeping them sorted by `key`. Each entry contains:
   * `key`: the exact cop identifier, such as `Lint/MultipleComparison`.
   * `name`: the cop name with spaces followed by its department in parentheses,
     such as `Multiple Comparison (Lint)`.
   * `description`: the cop's description, with HTML special characters escaped
     (for example `&` becomes `&amp;`). The loader wraps this text in an HTML paragraph.
   * `url`: the cop's public documentation URL. Existing entries use
     `https://www.rubydoc.info/gems/rubocop/RuboCop/Cop/<key>`.
   * `type` and `severity`: use the existing department mapping below, or explain
     any deliberate change in the pull request.

   | Department | Type | Severity |
   | --- | --- | --- |
   | Naming, Layout, Migration | CODE_SMELL | INFO |
   | Bundler, Gemspec, Metrics, Style | CODE_SMELL | MINOR |
   | Lint, Performance, Rails | CODE_SMELL | MAJOR |
   | Security | VULNERABILITY | MAJOR |

   Choose and review an appropriate mapping for new departments. Preserve existing keys
   unless intentionally removing support for them; older reports can still reference them.
4. From the repository root, run the rule-loading and report-import tests:

   ```shell
   ./gradlew :sonar-ruby-plugin:test --tests '*RuboCop*'
   ```

   If adding or removing entries, update the expected rule count in
   `RuboCopRulesDefinitionTest`. Review the final `rules.json` diff before committing.

The report files under `its/plugin/projects/externalreport/rubocop` and
`sonar-ruby-plugin/src/test/resources/externalreport/rubocop` are test fixtures;
refresh them only when the report-import behavior under test changes.
