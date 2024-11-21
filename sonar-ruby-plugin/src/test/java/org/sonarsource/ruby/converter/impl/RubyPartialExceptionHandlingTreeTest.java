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
package org.sonarsource.ruby.converter.impl;

import org.junit.jupiter.api.Test;
import org.sonarsource.slang.api.CatchTree;
import org.sonarsource.slang.api.Tree;
import org.sonarsource.slang.impl.CatchTreeImpl;
import org.sonarsource.slang.impl.IdentifierTreeImpl;
import org.sonarsource.slang.impl.LiteralTreeImpl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class RubyPartialExceptionHandlingTreeTest {

  @Test
  void test() {
    Tree tryBlock = new LiteralTreeImpl(null, "1");
    Tree catchBlock = new LiteralTreeImpl(null, "2");
    CatchTree catchTree = new CatchTreeImpl(null, null, catchBlock, null);

    RubyPartialExceptionHandlingTree tree1 = new RubyPartialExceptionHandlingTree(null, emptyList());
    assertThat(tree1.children()).isEmpty();
    assertThat(tree1.metaData()).isNull();

    tree1.setFinallyBlock(new IdentifierTreeImpl(null, "x"));
    assertThat(tree1.children()).hasSize(1);
    assertTree(tree1.children().get(0)).isIdentifier("x");

    RubyPartialExceptionHandlingTree tree2 = new RubyPartialExceptionHandlingTree(tryBlock, singletonList(catchTree));
    assertThat(tree2.children()).containsExactly(tryBlock, catchTree);
  }

}