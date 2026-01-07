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
package org.sonarsource.ruby.converter.adapter;

import org.junit.jupiter.api.Test;
import org.sonarsource.analyzer.commons.TokenLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.ruby.converter.adapter.CommentAdapter.getContentText;
import static org.sonarsource.ruby.converter.adapter.CommentAdapter.getContentTextRange;
import static org.sonarsource.slang.testing.RangeAssert.assertRange;

class CommentAdapterTest {
  @Test
  void get_content_text() {
    assertThat(getContentText("=begin\r\nXX\r\n=end\r\n")).isEqualTo("\r\nXX\r\n");
    assertThat(getContentText("=begin\nXX\n=end\n")).isEqualTo("\nXX\n");
    assertThat(getContentText("")).isEmpty();
  }

  @Test
  void get_content_text_range() {
    String text = "=begin\r\nABC\r\n=end\r\n";
    assertRange(getContentTextRange(text, new TokenLocation(1, 0, text), "\r\nABC\r\n")).hasRange(1, 6, 3, 0);
    text = "=begin\nA\n=end\n";
    assertRange(getContentTextRange(text, new TokenLocation(1, 0, text), "\nA\n")).hasRange(1, 6, 3, 0);
    text = "=begin\n=end\n";
    assertRange(getContentTextRange(text, new TokenLocation(1, 0, text), "\n")).hasRange(1, 6, 2, 0);
  }
}
