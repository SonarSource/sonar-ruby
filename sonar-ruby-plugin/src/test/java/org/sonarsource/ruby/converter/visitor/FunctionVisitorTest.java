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
package org.sonarsource.ruby.converter.visitor;


import org.junit.jupiter.api.Test;
import org.sonarsource.ruby.converter.AbstractRubyConverterTest;
import org.sonarsource.slang.api.FunctionDeclarationTree;
import org.sonarsource.slang.api.NativeTree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.slang.testing.RangeAssert.assertRange;
import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class FunctionVisitorTest extends AbstractRubyConverterTest {

  @Test
  void simple_function() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(p)\n puts 'hello'\nend");
    assertTree(tree.name()).isIdentifier("foo");
    assertThat(tree.isConstructor()).isFalse();
    assertTree(tree.name()).hasTextRange(1, 4, 1, 7);
    assertThat(tree.modifiers()).isEmpty();
    assertThat(tree.returnType()).isNull();
    assertThat(tree.formalParameters()).hasSize(1);
    assertTree(tree.formalParameters().get(0)).isEquivalentTo(parameter("p"));
    assertThat(tree.body().statementOrExpressions()).hasSize(1);
    assertThat(((NativeTree) tree.body().statementOrExpressions().get(0)).nativeKind()).isEqualTo(nativeKind("send"));
    assertThat(tree.nativeChildren()).isEmpty();
  }

  @Test
  void constructor() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def initialize(p)\n puts p\nend");
    assertTree(tree.name()).isIdentifier("initialize");
    assertThat(tree.isConstructor()).isTrue();
  }

  @Test
  void function_without_arguments() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo; end");
    assertTree(tree.name()).isIdentifier("foo");
    assertThat(tree.modifiers()).isEmpty();
    assertThat(tree.returnType()).isNull();
    assertThat(tree.formalParameters()).isEmpty();
    assertThat(tree.body()).isNotNull();
    assertThat(tree.body().statementOrExpressions()).isEmpty();
    assertRange(tree.body().textRange()).hasRange(1, 0, 1, 12);
    assertThat(tree.nativeChildren()).isEmpty();
  }

  @Test
  void singleton_method() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def a.foo\n puts 'hello'\n puts 'hello'\nend");
    assertTree(tree.name()).isIdentifier("foo");
    assertThat(tree.modifiers()).isEmpty();
    assertThat(tree.returnType()).isNull();
    assertThat(tree.formalParameters()).isEmpty();
    assertThat(tree.body().statementOrExpressions()).hasSize(2);
    assertTree(tree.body()).isBlock(NativeTree.class, NativeTree.class);
    assertThat(tree.nativeChildren()).hasSize(1);
    assertTree(tree.nativeChildren().get(0)).isEquivalentTo(sendToIdentifier("a"));
  }

}
