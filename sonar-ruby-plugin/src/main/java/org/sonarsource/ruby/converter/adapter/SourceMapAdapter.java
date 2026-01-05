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

public class SourceMapAdapter extends JRubyObjectAdapter<IRubyObject> {

  public SourceMapAdapter(Ruby runtime, IRubyObject underlyingRubyObject) {
    super(runtime, underlyingRubyObject);
  }

  public RangeAdapter getRange() {
    return getRange("expression");
  }

  public RangeAdapter getRange(String attribute) {
    return new RangeAdapter(runtime, getFromUnderlying(attribute, IRubyObject.class));
  }

}
