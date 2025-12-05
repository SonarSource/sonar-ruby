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
package org.sonarsource.ruby.converter.visitor;

import org.junit.jupiter.api.Test;
import org.sonarsource.ruby.converter.AbstractRubyConverterTest;
import org.sonarsource.slang.api.FunctionDeclarationTree;
import org.sonarsource.slang.api.ThrowTree;
import org.sonarsource.slang.api.Tree;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class RaiseVisitorTest extends AbstractRubyConverterTest {

  @Test
  void test_raise() {
    FunctionDeclarationTree tree = ((FunctionDeclarationTree) rubyStatement("""
      def foo
        raise 42
      end"""));
    Tree raise = tree.body().statementOrExpressions().get(0);
    assertTree(raise).isInstanceOf(ThrowTree.class);
    ThrowTree throwTree = (ThrowTree) raise;
    assertTree(throwTree.body()).isEquivalentTo(integerLiteral("42"));
    assertThat(throwTree.keyword().text()).isEqualTo("raise");
  }

  @Test
  void test_raise_multi_value() {
    FunctionDeclarationTree tree = ((FunctionDeclarationTree) rubyStatement("""
      def foo
        raise 42, 43
      end"""));
    Tree raise = tree.body().statementOrExpressions().get(0);
    assertTree(raise).isInstanceOf(ThrowTree.class);
    ThrowTree throwTree = (ThrowTree) raise;
    assertTree(throwTree.body()).isEquivalentTo(nativeTree(nativeKind("raise"), asList(
      integerLiteral("42"),
      integerLiteral("43"))));
    assertThat(throwTree.keyword().text()).isEqualTo("raise");
  }

  @Test
  void test_raise_null() {
      ThrowTree tree = (ThrowTree) rubyStatement("raise");
      assertThat(tree.body()).isNull();
  }
}
