/*
 * SonarSource Ruby
 * Copyright (C) 2018-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
import org.junit.jupiter.api.Test;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInActiveRule;
import org.sonar.plugins.ruby.api.RubyProfileRegistrar;

import static org.assertj.core.api.Assertions.assertThat;

class RubyProfileDefinitionTest {

  @Test
  void profile() {
    BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
    new RubyProfileDefinition().define(context);
    BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = context.profile("ruby", "Sonar way");

    assertThat(profile.rules()).extracting("repoKey").containsOnly("ruby");
    assertThat(profile.rules()).hasSizeGreaterThan(1);
    assertThat(profile.rules()).extracting(BuiltInActiveRule::ruleKey).contains("S1135");
  }

  @Test
  void profileWithRegistrarAddingAdditionalRules() {
    // Create a test registrar that adds custom rules to the built-in profile
    RubyProfileRegistrar testRegistrar = registrarContext -> {
      RuleKey customRule1 = RuleKey.of("custom-repo", "CUSTOM001");
      RuleKey customRule2 = RuleKey.of("another-repo", "ANOTHER001");
      registrarContext.registerDefaultQualityProfileRules(List.of(customRule1, customRule2));
    };

    // Create profile definition with the registrar
    BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
    new RubyProfileDefinition(new RubyProfileRegistrar[]{testRegistrar}).define(context);
    BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = context.profile("ruby", "Sonar way");

    // Verify that the profile contains both default rules and additional rules from registrar
    assertThat(profile.rules()).hasSizeGreaterThan(2);
    assertThat(profile.rules()).extracting("repoKey").contains("ruby", "custom-repo", "another-repo");
    assertThat(profile.rules()).extracting(BuiltInActiveRule::ruleKey).contains("S1135", "CUSTOM001", "ANOTHER001");
  }

}
