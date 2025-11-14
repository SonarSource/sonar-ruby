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
package org.sonarsource.ruby.checks;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.slang.api.FunctionDeclarationTree;
import org.sonarsource.slang.api.IdentifierTree;
import org.sonarsource.slang.api.Tree;
import org.sonarsource.slang.checks.UnusedLocalVariableCheck;
import org.sonarsource.slang.checks.api.InitContext;
import org.sonarsource.slang.checks.utils.FunctionUtils;
import org.sonarsource.slang.utils.SyntacticEquivalence;

public class UnusedLocalVariableRubyCheck extends UnusedLocalVariableCheck {

  @Override
  public void initialize(InitContext init) {
    init.register(FunctionDeclarationTree.class, (ctx, functionDeclarationTree) -> {

      if (ctx.ancestors().stream().anyMatch(FunctionDeclarationTree.class::isInstance)) {
        return;
      }

      Set<IdentifierTree> variableIdentifiers = getVariableIdentifierTrees(functionDeclarationTree);
      Set<Tree> identifierTrees = getIdentifierTrees(functionDeclarationTree, variableIdentifiers);

      List<IdentifierTree> unusedVariables = variableIdentifiers.stream()
        .filter(variable -> identifierTrees.stream().noneMatch(identifier -> SyntacticEquivalence.areEquivalent(variable, identifier)))
        .collect(Collectors.toList());

      if (unusedVariables.isEmpty()) {
        return;
      }

      // the unused variables may actually be used inside interpolated strings, eval or prepared statements
      Set<String> stringLiteralTokens = FunctionUtils.getStringsTokens(functionDeclarationTree, Constants.SPECIAL_STRING_DELIMITERS);
      unusedVariables.stream()
        .filter(variable -> !stringLiteralTokens.contains(variable.name()))
        .forEach(identifier -> ctx.reportIssue(identifier, "Remove this unused \"" + identifier.name() + "\" local variable."));
    });
  }



}
