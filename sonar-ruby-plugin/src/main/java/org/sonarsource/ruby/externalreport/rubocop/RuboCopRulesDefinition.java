/*
 * SonarSource Ruby
 * Copyright (C) 2018-2026 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonarsource.ruby.externalreport.rubocop;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.ExternalRuleLoader;
import org.sonarsource.ruby.plugin.RubyPlugin;

import static org.sonarsource.ruby.externalreport.rubocop.RuboCopSensor.LINTER_KEY;
import static org.sonarsource.ruby.externalreport.rubocop.RuboCopSensor.LINTER_NAME;

public class RuboCopRulesDefinition implements RulesDefinition {

  private static final String RULES_JSON = "org/sonar/l10n/ruby/rules/rubocop/rules.json";

  private static final String RULE_REPOSITORY_LANGUAGE = RubyPlugin.RUBY_LANGUAGE_KEY;

  static final ExternalRuleLoader RULE_LOADER = new ExternalRuleLoader(LINTER_KEY, LINTER_NAME, RULES_JSON, RULE_REPOSITORY_LANGUAGE);

  @Override
  public void define(Context context) {
    RULE_LOADER.createExternalRuleRepository(context);
  }

}
