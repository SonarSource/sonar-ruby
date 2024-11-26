/*
 * SonarSource Ruby
 * Copyright (C) 2018-2024 SonarSource SA
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

import org.junit.jupiter.api.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class RuboCopRulesDefinitionTest {

  @Test
  void rubocop_lint_external_repository() {
    RulesDefinition.Context context = new RulesDefinition.Context();
    RuboCopRulesDefinition rulesDefinition = new RuboCopRulesDefinition();
    rulesDefinition.define(context);

    assertThat(context.repositories()).hasSize(1);
    RulesDefinition.Repository repository = context.repository("external_rubocop");
    assertThat(repository.name()).isEqualTo("RuboCop");
    assertThat(repository.language()).isEqualTo("ruby");
    assertThat(repository.isExternal()).isTrue();
    assertThat(repository.rules()).hasSize(542);

    RulesDefinition.Rule rule = repository.rule("Lint/MultipleComparison");
    assertThat(rule).isNotNull();
    assertThat(rule.name()).isEqualTo("Multiple Comparison (Lint)");
    assertThat(rule.type()).isEqualTo(RuleType.CODE_SMELL);
    assertThat(rule.severity()).isEqualTo("MAJOR");
    assertThat(rule.htmlDescription()).isEqualTo("<p>Use `&amp;&amp;` operator to compare multiple values.</p> " +
      "<p>See more at the <a href=\"https://www.rubydoc.info/gems/rubocop/RuboCop/Cop/Lint/MultipleComparison\">RuboCop website</a>.</p>");
    assertThat(rule.tags()).isEmpty();
    assertThat(rule.debtRemediationFunction().baseEffort()).isEqualTo("5min");
  }

}
