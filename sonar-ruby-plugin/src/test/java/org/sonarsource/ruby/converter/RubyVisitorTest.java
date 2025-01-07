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
package org.sonarsource.ruby.converter;


import org.junit.jupiter.api.Test;
import org.sonarsource.slang.api.TopLevelTree;
import org.sonarsource.slang.api.Tree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class RubyVisitorTest extends AbstractRubyConverterTest {

  @Test
  void top_level_tree() {
    assertTree(converter.parse(("true\nfalse"))).isInstanceOf(TopLevelTree.class);
    assertTree(converter.parse(("true\r\nfalse"))).isInstanceOf(TopLevelTree.class);
  }

  @Test
  void parse_with_missing_node() {
    Tree tree = converter.parse("def is_root?\nend"); // method has null argument list
    assertThat(tree).isNotNull();
  }

  @Test
  void singletons() {
    assertTree(rubyStatement("nil")).isEquivalentTo(nativeTree("nil", "nil"));
  }

}
