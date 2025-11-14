/*
 * SonarSource Ruby
 * Copyright (C) 2018-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource Sàrl.
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
import javax.annotation.CheckForNull;
import org.jruby.runtime.builtin.IRubyObject;
import org.sonarsource.slang.api.TextRange;

public interface AstNode {
  String type();

  @CheckForNull
  TextRange textRange();

  @CheckForNull
  TextRange textRangeForAttribute(String attribute);

  String asString();

  String source();

  IRubyObject node();

  List availableAttributes();
}
