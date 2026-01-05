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
package org.sonarsource.ruby.checks;

import java.util.List;
import java.util.Set;
import org.sonarsource.ruby.converter.RubyNativeKind;
import org.sonarsource.slang.api.FunctionDeclarationTree;
import org.sonarsource.slang.api.NativeTree;
import org.sonarsource.slang.api.ParameterTree;
import org.sonarsource.slang.checks.UnusedFunctionParameterCheck;
import org.sonarsource.slang.checks.api.CheckContext;
import org.sonarsource.slang.checks.api.InitContext;
import org.sonarsource.slang.checks.utils.FunctionUtils;
import org.sonarsource.slang.impl.BlockTreeImpl;
import org.sonarsource.slang.impl.TopLevelTreeImpl;

public class UnusedFunctionParameterRubyCheck extends UnusedFunctionParameterCheck {

  // native tree for call to 'super' method
  private static final RubyNativeKind SUPER_NATIVE_KIND = new RubyNativeKind("zsuper");

  @Override
  public void initialize(InitContext init) {
    init.register(FunctionDeclarationTree.class, (ctx, functionDeclarationTree) -> {
      if (functionDeclarationTree.isConstructor()
        || shouldBeIgnored(ctx, functionDeclarationTree)
        || hasCallToSuper(functionDeclarationTree)) {
        return;
      }

      List<ParameterTree> unusedParameters = getUnusedParameters(functionDeclarationTree);

      if (unusedParameters.isEmpty()) {
        return;
      }

      // the unused parameters may actually be used inside interpolated strings, eval or prepared statements
      Set<String> stringLiteralTokens = FunctionUtils.getStringsTokens(functionDeclarationTree, Constants.SPECIAL_STRING_DELIMITERS);
      unusedParameters.stream()
         // In Ruby, an identifier is always present, so no need to check for null here
        .filter(param -> !stringLiteralTokens.contains(param.identifier().name()))
        .forEach(identifier -> reportUnusedParameters(ctx, unusedParameters));
    });
  }

  private static boolean hasCallToSuper(FunctionDeclarationTree functionDeclarationTree) {
    return functionDeclarationTree.descendants()
            .filter(NativeTree.class::isInstance)
            .map(NativeTree.class::cast)
            .map(NativeTree::nativeKind)
            .anyMatch(SUPER_NATIVE_KIND::equals);
  }

  @Override
  protected boolean isValidFunctionForRule(CheckContext ctx, FunctionDeclarationTree tree) {
    return parentIsBlockUnderTopLevel(ctx) || super.isValidFunctionForRule(ctx, tree);
  }

  private static boolean parentIsBlockUnderTopLevel(CheckContext ctx) {
    return ctx.parent() instanceof BlockTreeImpl &&
      ctx.ancestors().size() == 2 &&
      ctx.ancestors().getLast() instanceof TopLevelTreeImpl;
  }

}
