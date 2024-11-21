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
package org.sonarsource.ruby.converter.visitor;

import org.junit.jupiter.api.Test;
import org.sonarsource.ruby.converter.AbstractRubyConverterTest;
import org.sonarsource.slang.api.NativeTree;

import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class JumpVisitorTest extends AbstractRubyConverterTest {

  @Test
  void without_expression() {
    assertTree(rubyStatement("break")).isEquivalentTo(slangStatement("break;"));
    assertTree(rubyStatement("next")).isEquivalentTo(slangStatement("continue;"));
  }

  @Test
  void with_expression() {
    assertTree(rubyStatement("break x")).isInstanceOf(NativeTree.class);
    assertTree(rubyStatement("next x")).isInstanceOf(NativeTree.class);
    assertTree(rubyStatement("next x")).isEquivalentTo(rubyStatement("next x"));
  }

}
