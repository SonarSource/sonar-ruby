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
package org.sonarsource.ruby.plugin;

import java.util.List;

import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;
import org.sonarsource.slang.checks.utils.Language;
import org.sonarsource.slang.plugin.RulesDefinitionUtils;

public class RubyRulesDefinition implements RulesDefinition {

  private static final String RESOURCE_FOLDER = "org/sonar/l10n/ruby/rules/ruby";

  private final SonarRuntime runtime;

  public RubyRulesDefinition(SonarRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public void define(Context context) {
    NewRepository repository = context
      .createRepository(RubyPlugin.RUBY_REPOSITORY_KEY, RubyPlugin.RUBY_LANGUAGE_KEY)
      .setName(RubyPlugin.REPOSITORY_NAME);
    RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_FOLDER, RubyProfileDefinition.PATH_TO_JSON, runtime);

    List<Class<?>> checks = RubyCheckList.checks();
    ruleMetadataLoader.addRulesByAnnotatedClass(repository, checks);

    RulesDefinitionUtils.setDefaultValuesForParameters(repository, checks, Language.RUBY);

    repository.done();
  }
}
