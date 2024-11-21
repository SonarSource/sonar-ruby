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
package org.sonarsource.ruby.plugin;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInActiveRule;

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

}
