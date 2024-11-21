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
package org.sonarsource.ruby.converter.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.slang.api.CatchTree;
import org.sonarsource.slang.api.Tree;
import org.sonarsource.slang.api.TreeMetaData;

public class RubyPartialExceptionHandlingTree implements Tree {

  private final List<CatchTree> catchBlocks;
  private Tree tryBlock;
  private Tree finallyBlock;

  public RubyPartialExceptionHandlingTree(@Nullable Tree tryBlock, List<CatchTree> catchBlocks) {
    this.catchBlocks = catchBlocks;
    this.tryBlock = tryBlock;
  }

  @CheckForNull
  public Tree tryBlock() {
    return tryBlock;
  }

  public List<CatchTree> catchBlocks() {
    return catchBlocks;
  }

  @CheckForNull
  public Tree finallyBlock() {
    return finallyBlock;
  }

  @Override
  public List<Tree> children() {
    List<Tree> children = new ArrayList<>();
    if (tryBlock != null) {
      children.add(tryBlock);
    }
    children.addAll(catchBlocks);
    if (finallyBlock != null) {
      children.add(finallyBlock);
    }
    return children;
  }

  @CheckForNull
  @Override
  public TreeMetaData metaData() {
    return null;
  }

  public void setFinallyBlock(Tree finallyBlock) {
    this.finallyBlock = finallyBlock;
  }

}
