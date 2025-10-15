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
package org.sonar.plugins.ruby.api;

import java.util.Collection;
import org.sonar.api.Beta;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.ServerSide;
import org.sonarsource.api.sonarlint.SonarLintSide;

/**
 * This class can be extended to provide additional rule keys in the builtin default quality profile.
 *
 * <pre>
 *   {@code
 *     public void register(RegistrarContext registrarContext) {
 *       registrarContext.registerDefaultQualityProfileRules(ruleKeys);
 *     }
 *   }
 * </pre>
 *
 */
@Beta
@SonarLintSide
@ServerSide
public interface RubyProfileRegistrar {
  /**
   * This method is called on server side and during an analysis to modify the builtin default quality profile for Ruby.
   */
  void register(RegistrarContext registrarContext);

  interface RegistrarContext {
    /**
     * Registers additional rules into the "Sonar Way" default quality profile for Ruby.
     *
     * @param ruleKeys additional rule keys
     */
    void registerDefaultQualityProfileRules(Collection<RuleKey> ruleKeys);
  }
}
