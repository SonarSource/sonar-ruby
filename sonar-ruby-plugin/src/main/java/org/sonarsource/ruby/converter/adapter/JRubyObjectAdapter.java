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
package org.sonarsource.ruby.converter.adapter;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public abstract class JRubyObjectAdapter<T extends IRubyObject> {

  protected final Ruby runtime;
  protected final T underlyingRubyObject;

  protected JRubyObjectAdapter(Ruby runtime, T underlyingRubyObject) {
    this.runtime = runtime;
    this.underlyingRubyObject = underlyingRubyObject;
  }

  protected <U> U getFromUnderlying(String attribute, Class<U> clazz) {
    return (U) JavaEmbedUtils.invokeMethod(runtime, underlyingRubyObject, attribute, null, clazz);
  }

  public boolean isNull() {
    return underlyingRubyObject == null;
  }

}
