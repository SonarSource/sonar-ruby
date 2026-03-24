/*
 * SonarSource Ruby
 * Copyright (C) 2018-2026 SonarSource Sàrl
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
package org.sonarsource.ruby.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonarsource.ruby.converter.RubyConverter;
import org.sonarsource.slang.api.ASTConverter;
import org.sonarsource.slang.checks.api.SlangCheck;
import org.sonarsource.slang.plugin.SlangSensor;

public class RubySensor extends SlangSensor {

  private static final Logger LOG = LoggerFactory.getLogger(RubySensor.class);
  public static final String SKIP_PROPERTY_KEY = "sonar.ruby.internal.skipRubySensor";

  private final Checks<SlangCheck> checks;

  public RubySensor(SonarRuntime sonarRuntime, CheckFactory checkFactory, FileLinesContextFactory fileLinesContextFactory, NoSonarFilter noSonarFilter, RubyLanguage language) {
    super(sonarRuntime, noSonarFilter, fileLinesContextFactory, language);
    checks = checkFactory.create(RubyPlugin.RUBY_REPOSITORY_KEY);
    checks.addAnnotatedChecks((Iterable<?>) RubyCheckList.checks());
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    super.describe(descriptor);
    descriptor.onlyWhenConfiguration(configuration -> !shouldSkip(configuration));
  }

  @Override
  public void execute(SensorContext context) {
    if (shouldSkip(context.config())) {
      LOG.info("Skipping Ruby sensor because '{}' is enabled.", SKIP_PROPERTY_KEY);
      return;
    }
    super.execute(context);
  }

  @Override
  protected ASTConverter astConverter(SensorContext sensorContext) {
    return new RubyConverter();
  }

  @Override
  protected Checks<SlangCheck> checks() {
    return checks;
  }

  @Override
  protected String repositoryKey() {
    return RubyPlugin.RUBY_REPOSITORY_KEY;
  }

  private static boolean shouldSkip(Configuration configuration) {
    return configuration.getBoolean(SKIP_PROPERTY_KEY).orElse(true);
  }

}
