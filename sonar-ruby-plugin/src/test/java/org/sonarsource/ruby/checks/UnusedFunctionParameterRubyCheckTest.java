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
package org.sonarsource.ruby.checks;

import org.junit.jupiter.api.Test;

class UnusedFunctionParameterRubyCheckTest {

  @Test
  void test(){
    RubyVerifier.verify("UnusedFunctionParameter.rb", new UnusedFunctionParameterRubyCheck());
  }

  @Test
  void test_single_function(){
    RubyVerifier.verify("UnusedFunctionParameter.SingleFunction.rb", new UnusedFunctionParameterRubyCheck());
  }

  @Test
  void test_single_class(){
    RubyVerifier.verify("UnusedFunctionParameter.SingleClass.rb", new UnusedFunctionParameterRubyCheck());
  }
}
