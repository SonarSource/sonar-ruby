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
package org.sonarsource.ruby.plugin;

import org.junit.jupiter.api.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.PluginContextImpl;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

class RubyPluginTest {

  @Test
  void sonarqube_7_9_extensions() {
    SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(Version.create(7, 9), SonarQubeSide.SERVER, SonarEdition.COMMUNITY);
    Plugin.Context context = new Plugin.Context(runtime);
    new RubyPlugin().define(context);
    assertThat(context.getExtensions()).hasSize(12);
  }

  @Test
  void test_sonarlint() {
    SonarRuntime runtime = SonarRuntimeImpl.forSonarLint(Version.create(3, 9));
    Plugin.Context context = new PluginContextImpl.Builder().setSonarRuntime(runtime).build();
    new RubyPlugin().define(context);
    assertThat(context.getExtensions()).hasSize(5);
  }

}
