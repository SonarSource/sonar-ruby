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
import org.sonarsource.slang.api.FunctionDeclarationTree;
import org.sonarsource.slang.api.ParameterTree;
import org.sonarsource.slang.api.Tree;

import static java.util.Arrays.asList;
import static org.sonarsource.slang.testing.TreeAssert.assertTree;

class ArgVisitorTest extends AbstractRubyConverterTest {

  @Test
  void test_args() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(x) end");
    ParameterTree firstParameter = (ParameterTree) tree.formalParameters().get(0);
    assertTree(firstParameter).isEquivalentTo(parameter("x"));
  }

  @Test
  void test_optional_arg() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(x = 1) end");
    Tree firstParameter = tree.formalParameters().get(0);
    assertTree(firstParameter).isEquivalentTo(
        parameter("x", integerLiteral("1")));
  }

  @Test
  void test_rest_arg() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(*x) end");
    Tree firstParameter = tree.formalParameters().get(0);
    assertTree(firstParameter).isEquivalentTo(parameter("x"));
  }

  @Test
  void test_unnamed_rest_arg() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(*, arg) end");
    Tree firstParameter = tree.formalParameters().get(0);
    assertTree(firstParameter).isEquivalentTo(nativeTree("restarg", "*"));
    assertTree(tree.formalParameters().get(1)).isEquivalentTo(parameter("arg"));
  }

  @Test
  void test_kwarg() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(arg:, bar: 'default', **splat) end");
    Tree param1 = tree.formalParameters().get(0);
    assertTree(param1).isEquivalentTo(parameter("arg"));
    Tree param2 = tree.formalParameters().get(1);
    assertTree(param2).isEquivalentTo(parameter("bar", stringLiteral("'default'", "default")));
    Tree param3 = tree.formalParameters().get(2);
    assertTree(param3).isEquivalentTo(parameter("splat"));
  }

  @Test
  void test_unnamed_kw_rest_arg() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(**) end");
    Tree firstParameter = tree.formalParameters().get(0);
    assertTree(firstParameter).isEquivalentTo(nativeTree("kwrestarg", "**"));
  }

  @Test
  void block_arg() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def foo(&block) end");
    assertTree(tree.formalParameters().get(0)).isEquivalentTo(parameter("block"));
  }

  @Test
  void procarg() {
    Tree tree = rubyStatement("[].each do |number| number.odd? end");
    Tree blockArg = tree.children().get(1).children().get(0);
    assertTree(blockArg).isEquivalentTo(parameter("number"));
  }

  @Test
  void block_local_var() {
    Tree tree = rubyStatement("[].each do |number;x,y| number.odd? end");
    Tree args = tree.children().get(1);
    assertTree(args).isEquivalentTo(nativeTree(nativeKind("args"), asList(
      parameter("number"), parameter("x"), parameter("y"))));
  }

  @Test
  void deocmposition() {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) rubyStatement("def f(a, (foo, *bar)); end");
    assertTree(tree.formalParameters().get(0)).isEquivalentTo(parameter("a"));
    assertTree(tree.formalParameters().get(1)).isEquivalentTo(
      nativeTree(nativeKind("array"), asList(parameter("foo"), parameter("bar"))));
  }

  @Test
  void block_decomposition() {
    // this code is from ruling its/sources/ruby/discourse/app/models/permalink.rb:47
    Tree tree = rubyStatement("[].each do |(regex, sub)| url = url.sub(regex, sub) end");
    Tree args = tree.children().get(1);
    Tree procarg = args.children().get(0);
    assertTree(procarg.children().get(0)).isEquivalentTo(parameter("regex"));
    assertTree(procarg.children().get(1)).isEquivalentTo(parameter("sub"));

    tree = rubyStatement("[].each do |regex, sub| url = url.sub(regex, sub) end");
    args = tree.children().get(1);
    assertTree(args.children().get(0)).isEquivalentTo(parameter("regex"));
    assertTree(args.children().get(1)).isEquivalentTo(parameter("sub"));
  }


}
