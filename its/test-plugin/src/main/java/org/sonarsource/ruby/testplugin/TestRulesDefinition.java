/*
 * SonarSource Ruby
 * Copyright (C) 2018-2025 SonarSource SA
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

import org.sonar.api.server.rule.RulesDefinition;

/**
 * Defines a test rule repository with a single TEST001 rule.
 */
public class TestRulesDefinition implements RulesDefinition {

  static final String REPOSITORY_KEY = "ruby-test";
  static final String LANGUAGE_KEY = "ruby";
  private static final String REPOSITORY_NAME = "Test Ruby Rules";
  static final String RULE_KEY = "TEST001";

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY_KEY, LANGUAGE_KEY)
      .setName(REPOSITORY_NAME);

    // Define the TEST001 rule
    repository.createRule(RULE_KEY)
      .setName("Test Rule 001")
      .setHtmlDescription("This is a test rule for integration testing purposes.");

    repository.done();
  }
}

