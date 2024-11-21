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
import org.sonarsource.slang.api.BinaryExpressionTree.Operator;

import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class BinaryExpressionVisitorTest extends AbstractRubyConverterTest {

  @Test
  void comparison() {
    assertTree(rubyStatement("a == b")).isBinaryExpression(Operator.EQUAL_TO);
    assertTree(rubyStatement("a != b")).isBinaryExpression(Operator.NOT_EQUAL_TO);
    assertTree(rubyStatement("a < b")).isBinaryExpression(Operator.LESS_THAN);
    assertTree(rubyStatement("a > b")).isBinaryExpression(Operator.GREATER_THAN);
    assertTree(rubyStatement("a <= b")).isBinaryExpression(Operator.LESS_THAN_OR_EQUAL_TO);
    assertTree(rubyStatement("a >= b")).isBinaryExpression(Operator.GREATER_THAN_OR_EQUAL_TO);
  }

  @Test
  void arithmetic() {
    assertTree(rubyStatement("a + b")).isBinaryExpression(Operator.PLUS);
    assertTree(rubyStatement("a - b")).isBinaryExpression(Operator.MINUS);
    assertTree(rubyStatement("a * b")).isBinaryExpression(Operator.TIMES);
    assertTree(rubyStatement("a / b")).isBinaryExpression(Operator.DIVIDED_BY);
  }

  @Test
  void logical() {
    assertTree(rubyStatement("a && b")).isBinaryExpression(Operator.CONDITIONAL_AND);
    assertTree(rubyStatement("a || b")).isBinaryExpression(Operator.CONDITIONAL_OR);
    // NOTE: pairs &&/and and ||/or don't have the same priority, still the same tree is created
    assertTree(rubyStatement("a and b")).isBinaryExpression(Operator.CONDITIONAL_AND);
    assertTree(rubyStatement("a or b")).isBinaryExpression(Operator.CONDITIONAL_OR);
  }

  @Test
  void equivalent_to_slang() {
    assertTree(rubyStatement("1 < 2")).isEquivalentTo(slangStatements("1 < 2;").get(0));
  }

}
