/*
 * SonarSource Ruby
 * Copyright (C) 2018-2026 SonarSource Sàrl
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
import org.sonarsource.slang.api.BinaryExpressionTree.Operator;
import org.sonarsource.slang.api.NativeTree;
import org.sonarsource.slang.api.ParenthesizedExpressionTree;
import org.sonarsource.slang.api.Tree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class ParenthesizedVisitorTest extends AbstractRubyConverterTest {

  @Test
  void test() {
    ParenthesizedExpressionTree parenthesizedTree = (ParenthesizedExpressionTree) rubyStatement("(a + b)");
    assertTree(parenthesizedTree.expression()).isBinaryExpression(Operator.PLUS);
    assertThat(parenthesizedTree.leftParenthesis().text()).isEqualTo("(");
    assertThat(parenthesizedTree.rightParenthesis().text()).isEqualTo(")");

    parenthesizedTree = (ParenthesizedExpressionTree) rubyStatement("(1)");
    assertTree(parenthesizedTree.expression()).isLiteral("1");
    assertThat(parenthesizedTree.leftParenthesis().text()).isEqualTo("(");
    assertThat(parenthesizedTree.rightParenthesis().text()).isEqualTo(")");
  }

  @Test
  void not_expression_if_multiple_elements() {
    Tree beginAsStatementList = rubyStatement("(a; b;)");
    assertTree(beginAsStatementList).isBlock(NativeTree.class, NativeTree.class);
  }

}
