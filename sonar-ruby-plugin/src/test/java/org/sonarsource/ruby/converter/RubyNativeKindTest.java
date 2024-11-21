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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RubyNativeKindTest {

  @Test
  void kinds() {
    RubyNativeKind nativeKind = new RubyNativeKind("test");

    assertThat(nativeKind)
      .isEqualTo(new RubyNativeKind("test"))
      .isNotEqualTo(new RubyNativeKind("test_other"))
      .isNotNull()
      .isNotEqualTo(new Object())
      .hasSameHashCodeAs(new RubyNativeKind("test"));

    assertThat(nativeKind.hashCode()).isNotEqualTo(new RubyNativeKind("test_other").hashCode());

    assertThat(nativeKind).hasToString("test");
  }

}
