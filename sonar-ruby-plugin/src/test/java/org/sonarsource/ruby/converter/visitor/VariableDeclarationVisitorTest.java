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
import org.sonarsource.slang.api.AssignmentExpressionTree;
import org.sonarsource.slang.api.BlockTree;
import org.sonarsource.slang.api.ClassDeclarationTree;
import org.sonarsource.slang.api.FunctionDeclarationTree;
import org.sonarsource.slang.api.Tree;
import org.sonarsource.slang.api.VariableDeclarationTree;

import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class VariableDeclarationVisitorTest extends AbstractRubyConverterTest {

  @Test
  void declaration() {
    Tree tree = rubyStatement("def foo; a = 1; end; def self.bar; a = 1; end");
    assertTree(tree).isInstanceOf(BlockTree.class);
    FunctionDeclarationTree fooFunction = ((FunctionDeclarationTree) tree.children().get(0));
    assertTree(fooFunction.body().children().get(0)).isEquivalentTo(slangStatement("var a = 1;"));
    FunctionDeclarationTree barFunction = ((FunctionDeclarationTree) tree.children().get(1));
    assertTree(barFunction.body().children().get(0)).isEquivalentTo(slangStatement("var a = 1;"));
  }

  @Test
  void class_scope() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Test
          fooVar = 10
          barVar = 10

          define_method :foo do\s
            fooVar = 2
            puts fooVar
          end

      def bar; fooVar = 1; end
      barVar = 10
      end""");
    Tree body = tree.classTree().children().get(1);
    assertTree(body.children().get(0)).isInstanceOf(VariableDeclarationTree.class);  // fooVar
    assertTree(body.children().get(1)).isInstanceOf(VariableDeclarationTree.class);  // barVar

    BlockTree block = (BlockTree) body.children().get(2).children().get(1);
    assertTree(block.statementOrExpressions().get(0)).isInstanceOf(AssignmentExpressionTree.class); // fooVar

    FunctionDeclarationTree functionDeclarationTree = (FunctionDeclarationTree) body.children().get(3);
    assertTree(functionDeclarationTree.body().statementOrExpressions().get(0)).isInstanceOf(VariableDeclarationTree.class); // fooVar

    assertTree(body.children().get(4)).isInstanceOf(AssignmentExpressionTree.class); // barVar
  }

}
