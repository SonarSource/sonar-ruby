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

import java.util.Arrays;
import java.util.List;
import org.sonarsource.ruby.checks.UnusedFunctionParameterRubyCheck;
import org.sonarsource.ruby.checks.UnusedLocalVariableRubyCheck;
import org.sonarsource.slang.checks.BooleanLiteralCheck;
import org.sonarsource.slang.checks.CheckList;
import org.sonarsource.slang.checks.UnusedFunctionParameterCheck;
import org.sonarsource.slang.checks.UnusedLocalVariableCheck;
import org.sonarsource.slang.checks.UnusedPrivateMethodCheck;

public final class RubyCheckList {

  private RubyCheckList() {
    // utility class
  }

  static final Class[] RUBY_CHECK_BLACK_LIST = {
    BooleanLiteralCheck.class,
    UnusedPrivateMethodCheck.class,
    // Language specific implementation is provided.
    UnusedFunctionParameterCheck.class,
    UnusedLocalVariableCheck.class
  };

  static final List<Class<?>> RUBY_SPECIFIC_CHECKS = Arrays.asList(
    UnusedFunctionParameterRubyCheck.class,
    UnusedLocalVariableRubyCheck.class
  );

  public static List<Class<?>> checks() {
    List<Class<?>> list = CheckList.excludeChecks(RUBY_CHECK_BLACK_LIST);
    list.addAll(RUBY_SPECIFIC_CHECKS);
    return list;
  }

}
