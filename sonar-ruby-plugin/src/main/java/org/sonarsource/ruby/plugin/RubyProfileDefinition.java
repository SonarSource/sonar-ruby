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
package org.sonarsource.ruby.plugin;

import java.util.ArrayList;
import java.util.List;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.ruby.api.RubyProfileRegistrar;
import org.sonarsource.analyzer.commons.BuiltInQualityProfileJsonLoader;

public class RubyProfileDefinition implements BuiltInQualityProfilesDefinition {

  static final String PATH_TO_JSON = "org/sonar/l10n/ruby/rules/ruby/Sonar_way_profile.json";
  private final List<RuleKey> additionalRules = new ArrayList<>();

  public RubyProfileDefinition() {
    this(new RubyProfileRegistrar[]{});
  }

  public RubyProfileDefinition(RubyProfileRegistrar[] registrars) {
    for (var registrar : registrars) {
      registrar.register(additionalRules::addAll);
    }
  }

  @Override
  public void define(Context context) {
    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(RubyPlugin.PROFILE_NAME, RubyPlugin.RUBY_LANGUAGE_KEY);
    BuiltInQualityProfileJsonLoader.load(profile, RubyPlugin.RUBY_REPOSITORY_KEY, PATH_TO_JSON);
    additionalRules.forEach(ruleKey -> profile.activateRule(ruleKey.repository(), ruleKey.rule()));
    profile.done();
  }

}
