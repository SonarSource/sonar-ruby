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
package org.sonarsource.ruby.converter.adapter;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;
import org.sonarsource.slang.api.TextPointer;
import org.sonarsource.slang.api.TextRange;
import org.sonarsource.slang.impl.TextPointerImpl;
import org.sonarsource.slang.impl.TextRangeImpl;

public class RangeAdapter extends JRubyObjectAdapter<IRubyObject> {

  public RangeAdapter(Ruby runtime, IRubyObject underlyingRubyObject) {
    super(runtime, underlyingRubyObject);
  }

  int getLine() {
    return getFromUnderlying("line", IRubyObject.class).convertToInteger().getIntValue();
  }

  int getColumn() {
    return getFromUnderlying("column", IRubyObject.class).convertToInteger().getIntValue();
  }

  int getLastLine() {
    return getFromUnderlying("last_line", IRubyObject.class).convertToInteger().getIntValue();
  }

  int getLastColumn() {
    return getFromUnderlying("last_column", IRubyObject.class).convertToInteger().getIntValue();
  }

  public TextRange toTextRange() {
    TextPointer startPointer = new TextPointerImpl(getLine(), getColumn());
    TextPointer endPointer = new TextPointerImpl(getLastLine(), getLastColumn());
    return new TextRangeImpl(startPointer, endPointer);
  }

}
