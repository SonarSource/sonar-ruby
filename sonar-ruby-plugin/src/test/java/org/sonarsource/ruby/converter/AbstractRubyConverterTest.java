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
package org.sonarsource.ruby.converter;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.slang.testing.ThreadLocalLogTester;
import org.sonarsource.slang.api.IntegerLiteralTree;
import org.sonarsource.slang.api.NativeKind;
import org.sonarsource.slang.api.NativeTree;
import org.sonarsource.slang.api.ParameterTree;
import org.sonarsource.slang.api.StringLiteralTree;
import org.sonarsource.slang.api.TopLevelTree;
import org.sonarsource.slang.api.Tree;
import org.sonarsource.slang.api.TreeMetaData;
import org.sonarsource.slang.impl.IdentifierTreeImpl;
import org.sonarsource.slang.impl.IntegerLiteralTreeImpl;
import org.sonarsource.slang.impl.NativeTreeImpl;
import org.sonarsource.slang.impl.ParameterTreeImpl;
import org.sonarsource.slang.impl.StringLiteralTreeImpl;
import org.sonarsource.slang.impl.TokenImpl;
import org.sonarsource.slang.parser.SLangConverter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractRubyConverterTest {

  static RubyConverter converter;

  @RegisterExtension
  public ThreadLocalLogTester logTester = new ThreadLocalLogTester();

  @BeforeAll
  static void setUp() {
    converter = new RubyConverter();
  }

  @AfterAll
  static void tearDown() {
    converter.terminate();
  }

  protected Tree slangStatement(String innerCode) {
    List<Tree> statements = slangStatements(innerCode);
    assertThat(statements).hasSize(1);
    return statements.get(0);
  }

  protected List<Tree> slangStatements(String innerCode) {
    Tree tree = new SLangConverter().parse(innerCode);
    assertThat(tree).isInstanceOf(TopLevelTree.class);
    return tree.children();
  }

  protected Tree rubyStatement(String innerCode) {
    Tree tree = converter.parse(innerCode);
    assertThat(tree).isInstanceOf(TopLevelTree.class);
    assertThat(tree.children()).hasSize(1);
    return tree.children().get(0);
  }

  protected List<Tree> rubyStatements(String innerCode) {
    Tree tree = converter.parse(innerCode);
    assertThat(tree).isInstanceOf(TopLevelTree.class);
    return tree.children().get(0).children();
  }

  protected static IntegerLiteralTree integerLiteral(String value) {
    return new IntegerLiteralTreeImpl(null, value);
  }

  protected static StringLiteralTree stringLiteral(String value, String content) {
    return new StringLiteralTreeImpl(null, value, content);
  }

  protected static NativeTree nativeTree(NativeKind kind, List<Tree> children) {
    return new NativeTreeImpl(null, kind, children);
  }

  protected static ParameterTree parameter(String name) {
    return new ParameterTreeImpl(null, identifier(name), null);
  }

  protected static ParameterTree parameter(String name, Tree defaultValue) {
    return new ParameterTreeImpl(null, identifier(name), null, defaultValue);
  }

  protected static IdentifierTreeImpl identifier(String name) {
    return new IdentifierTreeImpl(null, name);
  }

  protected static NativeTree nativeTree(NativeKind kind, String... tokens) {
    return new NativeTreeImpl(metaData(tokens), kind, emptyList());
  }

  protected static NativeTree nativeTree(String nativeKind, String... tokens) {
    return nativeTree(nativeKind(nativeKind), tokens);
  }

  protected static NativeKind nativeKind(String type) {
    return new RubyNativeKind(type);
  }

  protected static NativeTree sendToIdentifier(String identifierName) {
    return nativeTree(nativeKind("send"), asList(identifier(identifierName)));
  }

  private static TreeMetaData metaData(String... tokens) {
    TreeMetaData metaData = mock(TreeMetaData.class);
    when(metaData.tokens()).thenReturn(Stream.of(tokens)
      .map(text -> new TokenImpl(null, text, null))
      .collect(Collectors.toList()));
    return metaData;
  }

}
