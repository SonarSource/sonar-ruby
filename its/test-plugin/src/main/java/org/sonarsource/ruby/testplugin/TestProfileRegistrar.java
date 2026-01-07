/*
 * SonarSource Ruby
 * Copyright (C) 2018-2026 SonarSource Sàrl
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
package org.sonarsource.ruby.testplugin;

import java.util.List;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.ruby.api.RubyProfileRegistrar;

/**
 * Test implementation of RubyProfileRegistrar that adds a single rule to the default quality profile.
 */
public class TestProfileRegistrar implements RubyProfileRegistrar {

  @Override
  public void register(RegistrarContext registrarContext) {
    RuleKey ruleKey = RuleKey.of(TestRulesDefinition.REPOSITORY_KEY, TestRulesDefinition.RULE_KEY);
    registrarContext.registerDefaultQualityProfileRules(List.of(ruleKey));
  }
}

