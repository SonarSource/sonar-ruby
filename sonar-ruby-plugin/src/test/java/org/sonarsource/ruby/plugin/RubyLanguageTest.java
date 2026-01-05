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

import org.junit.jupiter.api.Test;
import org.sonar.api.config.internal.MapSettings;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RubyLanguageTest {

  @Test
  void test_suffixes_default() {
    RubyLanguage rubyLanguage = new RubyLanguage(new MapSettings().asConfig());
    assertThat(rubyLanguage.getFileSuffixes()).containsExactly(".rb");
  }

  @Test
  void test_suffixes_empty() {
    RubyLanguage rubyLanguage = new RubyLanguage(new MapSettings().setProperty(RubyPlugin.RUBY_FILE_SUFFIXES_KEY, "").asConfig());
    assertThat(rubyLanguage.getFileSuffixes()).containsExactly(".rb");
  }

}
