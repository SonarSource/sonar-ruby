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
import org.sonarsource.slang.api.AssignmentExpressionTree;
import org.sonarsource.slang.api.NativeTree;
import org.sonarsource.slang.api.VariableDeclarationTree;

import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class IdentifierVisitorTest extends AbstractRubyConverterTest {

  @Test
  void test() throws Exception {
    assertTree(((AssignmentExpressionTree) rubyStatement("$a = 1")).leftHandSide()).isIdentifier("$a");
    assertTree(((AssignmentExpressionTree) rubyStatement("@a = 1")).leftHandSide()).isIdentifier("@a");
    assertTree(((AssignmentExpressionTree) rubyStatement("@@a = 1")).leftHandSide()).isIdentifier("@@a");
    assertTree(((VariableDeclarationTree) rubyStatement("A = 1")).identifier()).isIdentifier("A");

    assertTree(((VariableDeclarationTree) rubyStatement("a = a")).initializer()).isIdentifier("a");
    assertTree(((VariableDeclarationTree) rubyStatement("a = b")).initializer()).isInstanceOf(NativeTree.class);

    assertTree(((VariableDeclarationTree) rubyStatement("a = @b")).initializer()).isIdentifier("@b");
    assertTree(((VariableDeclarationTree) rubyStatement("a = @@b")).initializer()).isIdentifier("@@b");
    assertTree(((VariableDeclarationTree) rubyStatement("a = B")).initializer()).isIdentifier("B");

  }
}
