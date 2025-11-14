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
package org.sonarsource.ruby.plugin;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFileFilter;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.WildcardPattern;

public class RubyExclusionsFileFilter implements InputFileFilter {

  private final WildcardPattern[] excludedPatterns;

  public RubyExclusionsFileFilter(Configuration configuration) {
    excludedPatterns = WildcardPattern.create(configuration.getStringArray(RubyPlugin.EXCLUSIONS_KEY));
  }

  @Override
  public boolean accept(InputFile inputFile) {
    return isNotRubyFile(inputFile) || isNotExcluded(inputFile.uri().toString());
  }

  private static boolean isNotRubyFile(InputFile inputFile) {
    return !RubyPlugin.RUBY_LANGUAGE_KEY.equals(inputFile.language());
  }

  public boolean isNotExcluded(String filePath) {
    return !WildcardPattern.match(excludedPatterns, filePath);
  }
}
