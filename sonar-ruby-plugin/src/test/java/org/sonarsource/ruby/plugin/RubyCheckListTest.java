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
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sonarsource.slang.testing.PackageScanner;

import static org.assertj.core.api.Java6Assertions.assertThat;

class RubyCheckListTest {

  private static final String RUBY_CHECKS_PACKAGE = "org.sonarsource.ruby.checks";

  @Test
  void ruby_checks_size() {
    Assertions.assertThat(RubyCheckList.checks()).hasSizeGreaterThanOrEqualTo(40);
  }

  @Test
  void ruby_specific_checks_are_added_to_check_list() {
    List<String> languageImplementation = PackageScanner.findSlangChecksInPackage(RUBY_CHECKS_PACKAGE);

    List<String> checkListNames = RubyCheckList.checks().stream().map(Class::getName).toList();
    List<String> rubySpecificChecks = RubyCheckList.RUBY_SPECIFIC_CHECKS.stream().map(Class::getName).toList();

    for (String languageCheck : languageImplementation) {
      assertThat(checkListNames).contains(languageCheck);
      assertThat(rubySpecificChecks).contains(languageCheck);
      assertThat(languageCheck).endsWith("RubyCheck");
    }
  }

  @Test
  void ruby_excluded_not_present() {
    List<Class<?>> checks = RubyCheckList.checks();
    for (Class excluded : RubyCheckList.RUBY_CHECK_BLACK_LIST) {
      assertThat(checks).doesNotContain(excluded);
    }
  }
}
