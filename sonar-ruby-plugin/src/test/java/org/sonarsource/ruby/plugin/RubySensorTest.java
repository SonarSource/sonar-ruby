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
package org.sonarsource.ruby.plugin;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.error.AnalysisError;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.issue.internal.DefaultNoSonarFilter;
import org.sonar.api.config.internal.MapSettings;
import org.sonarsource.slang.testing.AbstractSensorTest;

import static org.assertj.core.api.Assertions.assertThat;

class RubySensorTest extends AbstractSensorTest {

  @Test
  void simple_file() {
    InputFile inputFile = createInputFile("file1.rb", """
      class C
      end
      puts '1 == 1'; puts 'abc'
      """);
    context.fileSystem().add(inputFile);
    sensor(checkFactory()).execute(context);

    assertThat(context.highlightingTypeAt(inputFile.key(), 1, 0)).containsExactly(TypeOfText.KEYWORD);
    assertThat(context.highlightingTypeAt(inputFile.key(), 1, 5)).isEmpty();

    // FIXME
    //assertThat(logTester.logs()).contains("1 source files to be analyzed");
  }

  @Test
  void test_access_modifiers_are_highlighted() {
    String source = """
      class Foo
        def is_public_by_default()
          public = "private"
          variable = public
          puts "Hello"
        end

        private

        def is_private()
          puts "Hello !"
        end

        protected

        def is_protected()
          puts "Hello !"
        end

        public

        def is_public_again()
          @protected = "protected"
        end
      end
      """;
    InputFile inputFile = createInputFile("file_with_modifiers.rb", source);
    context.fileSystem().add(inputFile);
    sensor(checkFactory()).execute(context);

    // Access modifiers are hightlighted
    assertThat(context.highlightingTypeAt(inputFile.key(), 8, 2)).containsExactly(TypeOfText.KEYWORD);
    assertThat(context.highlightingTypeAt(inputFile.key(), 14, 2)).containsExactly(TypeOfText.KEYWORD);
    assertThat(context.highlightingTypeAt(inputFile.key(), 20, 2)).containsExactly(TypeOfText.KEYWORD);

    // Variables named public, private and protected are also hightlighted
    assertThat(context.highlightingTypeAt(inputFile.key(), 3, 4)).containsExactly(TypeOfText.KEYWORD);
    assertThat(context.highlightingTypeAt(inputFile.key(), 4, 15)).containsExactly(TypeOfText.KEYWORD);
    // Attributes named public, private and protected are not hightlighted
    assertThat(context.highlightingTypeAt(inputFile.key(), 23, 4)).isEmpty();
  }

  @Test
  void test_fail_parsing() {
    InputFile inputFile = createInputFile("file1.rb", "{ <!REDECLARATION!>FOO<!>,<!REDECLARATION!>FOO<!> }");
    context.fileSystem().add(inputFile);
    CheckFactory checkFactory = checkFactory("S1764");
    sensor(checkFactory).execute(context);
    Collection<AnalysisError> analysisErrors = context.allAnalysisErrors();
    assertThat(analysisErrors).hasSize(1);
    AnalysisError analysisError = analysisErrors.iterator().next();
    assertThat(analysisError.inputFile()).isEqualTo(inputFile);
    assertThat(analysisError.message()).isEqualTo("Unable to parse file: file1.rb");
    TextPointer textPointer = analysisError.location();
    assertThat(textPointer).isNotNull();
    assertThat(textPointer.line()).isEqualTo(1);
    assertThat(textPointer.lineOffset()).isEqualTo(2);

    assertThat(logTester.logs()).contains(String.format("Unable to parse file: %s. Parse error at position 1:2", inputFile.uri()));
  }


  @Override
  protected String repositoryKey() {
    return RubyPlugin.RUBY_REPOSITORY_KEY;
  }

  @Override
  protected RubyLanguage language() {
    return new RubyLanguage(new MapSettings().asConfig());
  }

  private RubySensor sensor(CheckFactory checkFactory) {
    return new RubySensor(SQ_LTS_RUNTIME, checkFactory, fileLinesContextFactory, new DefaultNoSonarFilter(), language());
  }

}
