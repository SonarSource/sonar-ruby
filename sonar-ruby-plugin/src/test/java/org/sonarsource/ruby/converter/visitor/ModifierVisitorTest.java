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
import org.sonarsource.slang.api.*;

import java.util.List;
import org.sonarsource.slang.impl.ClassDeclarationTreeImpl;

import static org.assertj.core.api.Assertions.assertThat;

class ModifierVisitorTest extends AbstractRubyConverterTest {

  @Test
  void test() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Foo
        def public_function(a)
          puts "Hello"
        end

        private

        def is_no
          puts "Hello!"
        end
      end""");

    NativeTree nativeClassTree = (NativeTree) tree.children().get(0);
    assertThat(nativeClassTree.nativeKind()).isEqualTo(nativeKind("class"));
    BlockTree blockTree = (BlockTree) nativeClassTree.children().get(1);
    List<Tree> blockTreeChildren = blockTree.children();
    assertThat(blockTreeChildren).hasSize(3);
    Tree modifierTree = blockTreeChildren.get(1);
    assertThat(modifierTree).isInstanceOf(ModifierTree.class);
    assertModifierIsPrivate(modifierTree);
    FunctionDeclarationTree noModifierFnTree = (FunctionDeclarationTree) blockTreeChildren.get(0);
    assertThat(noModifierFnTree.modifiers()).isEmpty();
    assertFnIsPrivate(blockTreeChildren.get(2));
  }

  @Test
  void testInlinePrivate() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Foo
        private def pick_coder(coder)
          case coder
          when nil, "json"
            ActiveSupport::JSON
          when "custom"
            DummyEncoder
          when "none"
            nil
          end
        end
      end""");

    NativeTree nativeClassTree = (NativeTree) tree.children().get(0);
    assertThat(nativeClassTree.nativeKind()).isEqualTo(nativeKind("class"));

    NativeTree modifierNativeTree = (NativeTree) nativeClassTree.children().get(1);
    assertThat(modifierNativeTree.nativeKind()).isEqualTo(nativeKind("modifier"));
    List<Tree> modifierNativeChildren = modifierNativeTree.children();
    assertThat(modifierNativeChildren).hasSize(2);

    assertModifierIsPrivate(modifierNativeChildren.get(0));
    assertFnIsPrivate(modifierNativeChildren.get(1));
  }

  @Test
  void testMultiplePrivate() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Foo
        private

        def private1
          puts "Hello!"
        end

        public

        def public1
          puts "Hello!"
        end

        private

        def private2
          puts "Hello!"
        end

      end""");

    NativeTree nativeClassTree = (NativeTree) tree.children().get(0);
    BlockTree blockTree = (BlockTree) nativeClassTree.children().get(1);
    List<Tree> blockTreeChildren = blockTree.children();
    assertFnIsPrivate(blockTreeChildren.get(1));
    assertFnIsPrivate(blockTreeChildren.get(5));
  }

  @Test
  void testPublicPrivate() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Foo
        private

        public

        def public
          puts "Hello!"
        end

      end""");

    NativeTree nativeClassTree = (NativeTree) tree.children().get(0);
    BlockTree blockTree = (BlockTree) nativeClassTree.children().get(1);
    List<Tree> blockTreeChildren = blockTree.children();
    assertFnIsPublic(blockTreeChildren.get(2));
  }

  @Test
  void testPrivateInInnerClass() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Foo
        class Inner

          def inner_public(a)
            puts "Hello!"
          end

          private

          def inner_private(a)
            puts "Hello!"
          end

        end
      end""");

    NativeTree nativeClassTree = (NativeTree) tree.children().get(0);
    ClassDeclarationTree innerClassDeclarationTree = (ClassDeclarationTreeImpl) nativeClassTree.children().get(1);
    NativeTree innerClassTree = (NativeTree) innerClassDeclarationTree.children().get(0);
    BlockTree blockTree = (BlockTree) innerClassTree.children().get(1);
    List<Tree> blockTreeChildren = blockTree.children();
    assertThat(blockTreeChildren).hasSize(3);
    assertFnIsPrivate(blockTreeChildren.get(2));
  }

  @Test
  void testProtected() {
    ClassDeclarationTree tree = (ClassDeclarationTree) rubyStatement("""
      class Foo
        protected

        def protected
          puts "Hello!"
        end

      end""");

    NativeTree nativeClassTree = (NativeTree) tree.children().get(0);
    BlockTree blockTree = (BlockTree) nativeClassTree.children().get(1);
    List<Tree> blockTreeChildren = blockTree.children();
    assertFnIsProtected(blockTreeChildren.get(1));
  }


  private void assertFnIsPrivate(Tree fnTree) {
    assertFnModifier(fnTree, ModifierTree.Kind.PRIVATE);
  }

  private void assertFnIsPublic(Tree fnTree) {
    assertFnModifier(fnTree, ModifierTree.Kind.PUBLIC);
  }

  private void assertFnIsProtected(Tree fnTree) {
    assertFnModifier(fnTree, ModifierTree.Kind.PROTECTED);
  }

  private void assertFnModifier(Tree fnTree, ModifierTree.Kind kind) {
    FunctionDeclarationTree tree = (FunctionDeclarationTree) fnTree;
    assertThat(((ModifierTree) tree.modifiers().get(0)).kind()).isEqualTo(kind);
  }

  private void assertModifierIsPrivate(Tree modifierTree) {
    assertThat(((ModifierTree) modifierTree).kind()).isEqualTo(ModifierTree.Kind.PRIVATE);
  }
}
