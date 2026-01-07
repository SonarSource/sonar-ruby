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

import org.sonar.api.Plugin;

/**
 * Minimalistic SonarQube plugin for testing purposes.
 */
public class TestPlugin implements Plugin {

  @Override
  public void define(Context context) {
    // Register the rules definition that defines the TEST001 rule
    context.addExtension(TestRulesDefinition.class);
    // Register the profile registrar that adds the rule to the default Ruby quality profile
    context.addExtension(TestProfileRegistrar.class);
  }
}

