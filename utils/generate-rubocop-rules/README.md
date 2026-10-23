## RuboCop

To regenerate RuboCop's:
* [`rubocop.yml`](src/main/resources/rubocop.yml)
* [`rules.json`](../../sonar-ruby-plugin/src/main/resources/org/sonar/l10n/ruby/rules/rubocop/rules.json)
* [`rubocop-report.json`](../../its/plugin/projects/externalreport/rubocop/rubocop-report.json)

Run the following commands from the repository root with Docker and JDK 17 or newer installed:

```shell

# remove the previously generated rubocop rule list
rm "utils/generate-rubocop-rules/src/main/resources/rubocop.yml"

# remove the previously generated rubocop analysis report
rm "its/plugin/projects/externalreport/rubocop/rubocop-report.json"

CMDS="echo '## Installing rubocop'"
CMDS="${CMDS} && gem install rubocop"
CMDS="${CMDS} && echo '## Generating rubocop.yml'"
CMDS="${CMDS} && cd /sonar-ruby/utils/generate-rubocop-rules/src/main/resources"
CMDS="${CMDS} && (rubocop --show-cops | grep -v 'MaximumRangeSize: .inf' > rubocop.yml)"
CMDS="${CMDS} && echo '## Generating rubocop-report.json'"
CMDS="${CMDS} && cd /sonar-ruby/its/plugin/projects/externalreport/rubocop"
CMDS="${CMDS} && rubocop --format json --fail-level fatal --out rubocop-report.json"
CMDS="${CMDS} && echo -n '## Rubocop version used: ' && rubocop --version"

docker run --rm -it -v "$PWD:/sonar-ruby:rw" "ruby:latest" /bin/bash -c "${CMDS}"

# convert the yaml rubocop rule list into json
./gradlew -p utils/generate-rubocop-rules build run
```
