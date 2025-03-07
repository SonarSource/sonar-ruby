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
package org.sonarsource.ruby.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.event.Level;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;
import org.sonarsource.slang.testing.ThreadLocalLogTester;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class SimpleCovSensorTest {

  private static final Path COVERAGE_DIR = Paths.get("src", "test", "resources", "coverage");
  private static final String MODULE_KEY = "/Absolute/Path/To/";

  @RegisterExtension
  public ThreadLocalLogTester logTester = new ThreadLocalLogTester();

  private SimpleCovSensor sensor;

  @BeforeEach
  void setup() {
    sensor = new SimpleCovSensor(new RubyExclusionsFileFilter(new MapSettings().asConfig()));
  }

  @Test
  void test_relative_report_path() throws IOException {
    SensorContextTester context = getSensorContext("resultset.json", "file1.rb");
    sensor.execute(context);

    String fileKey = MODULE_KEY + ":file1.rb";
    assertThat(context.lineHits(fileKey, 1)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 2)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 3)).isEqualTo(2);
    assertThat(context.lineHits(fileKey, 4)).isNull();
    assertThat(context.lineHits(fileKey, 5)).isNull();
    assertThat(context.lineHits(fileKey, 6)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 7)).isZero();

    assertThat(logTester.logs())
      .hasSize(1)
      .containsExactly(
        "Importing SimpleCov resultset JSON will not be supported from simplecov 18.0. Consider using the JSON formatter, available from SimpleCov 20.0");
  }

  @Test
  void test_reportPath_property_default() throws IOException {
    SensorContextTester context = getSensorContext("resultset.json", "file1.rb");
    // unset reportPaths value
    context.setSettings(new MapSettings());
    // simulate default value being set
    context.settings().setProperty(RubyPlugin.REPORT_PATHS_KEY, RubyPlugin.REPORT_PATHS_DEFAULT_VALUE);
    context.fileSystem().add(createInputFile("coverage/.resultset.json", fileContent(COVERAGE_DIR, "resultset.json")));
    sensor.execute(context);

    String fileKey = MODULE_KEY + ":file1.rb";
    assertThat(context.lineHits(fileKey, 1)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 2)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 3)).isEqualTo(2);
    assertThat(context.lineHits(fileKey, 4)).isNull();
    assertThat(context.lineHits(fileKey, 5)).isNull();
    assertThat(context.lineHits(fileKey, 6)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 7)).isZero();
  }

  @Test
  void test_reportPath_property_JSON_formatter() throws IOException {
    SensorContextTester context = getSensorContext("json_formatter.json", "file1.rb");
    sensor.execute(context);

    String fileKey = MODULE_KEY + ":file1.rb";
    assertThat(context.lineHits(fileKey, 1)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 2)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 3)).isEqualTo(2);
    assertThat(context.lineHits(fileKey, 4)).isNull();
    assertThat(context.lineHits(fileKey, 5)).isNull();
    assertThat(context.lineHits(fileKey, 6)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 7)).isZero();
  }

  @Test
  void test_absolute_report_path() throws IOException {
    Path baseDir = COVERAGE_DIR.toAbsolutePath();
    Path reportPath = baseDir.resolve("resultset.json");
    SensorContextTester context = getSensorContext(reportPath.toString(), "file1.rb");
    sensor.execute(context);

    String fileKey = MODULE_KEY + ":file1.rb";
    assertThat(context.lineHits(fileKey, 1)).isEqualTo(1);
    assertThat(context.lineHits(fileKey, 3)).isEqualTo(2);
    assertThat(context.lineHits(fileKey, 4)).isNull();
    assertThat(context.lineHits(fileKey, 7)).isZero();
  }

  @Test
  void test_merged_resultset() throws IOException {
    SensorContextTester context = getSensorContext("merged_resultset.json", "file1.rb", "file2.rb");
    sensor.execute(context);

    String file1Key = MODULE_KEY + ":file1.rb";
    assertThat(context.lineHits(file1Key, 1)).isZero();
    assertThat(context.lineHits(file1Key, 2)).isNull();
    assertThat(context.lineHits(file1Key, 3)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 4)).isNull();
    assertThat(context.lineHits(file1Key, 5)).isNull();
    assertThat(context.lineHits(file1Key, 6)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 7)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 8)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 9)).isEqualTo(2);

    String file2Key = MODULE_KEY + ":file2.rb";
    assertThat(context.lineHits(file2Key, 1)).isEqualTo(3);
  }

  @Test
  void test_multi_resultsets() throws IOException {
    SensorContextTester context = getSensorContext("resultset_1.json, resultset_2.json", "file1.rb", "file2.rb");
    sensor.execute(context);

    String file1Key = MODULE_KEY + ":file1.rb";
    assertThat(context.lineHits(file1Key, 1)).isZero();
    assertThat(context.lineHits(file1Key, 2)).isNull();
    assertThat(context.lineHits(file1Key, 3)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 4)).isNull();
    assertThat(context.lineHits(file1Key, 5)).isNull();
    assertThat(context.lineHits(file1Key, 6)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 7)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 8)).isEqualTo(1);
    assertThat(context.lineHits(file1Key, 9)).isEqualTo(2);

    String file2Key = MODULE_KEY + ":file2.rb";
    assertThat(context.lineHits(file2Key, 1)).isEqualTo(3);
  }

  @Test
  void test_mergeHitsForLine() {
    assertThat(SimpleCovSensor.mergeHitsForLine(1, 2)).isEqualTo(3);
    assertThat(SimpleCovSensor.mergeHitsForLine(2, 1)).isEqualTo(3);
    assertThat(SimpleCovSensor.mergeHitsForLine(0, 0)).isZero();
    assertThat(SimpleCovSensor.mergeHitsForLine(null, 0)).isNull();
    assertThat(SimpleCovSensor.mergeHitsForLine(0, null)).isNull();
    assertThat(SimpleCovSensor.mergeHitsForLine(null, 1)).isEqualTo(1);
    assertThat(SimpleCovSensor.mergeHitsForLine(1, null)).isEqualTo(1);
  }

  @Test
  void no_measure_on_files_not_in_context() throws IOException {
    SensorContextTester context = spy(getSensorContext("additional_file_resultset.json", "file2.rb"));
    sensor.execute(context);

    // assert that newCoverage method is called only once on file2
    verify(context, times(1)).newCoverage();
    assertThat(context.lineHits(MODULE_KEY + ":file2.rb", 1)).isEqualTo(5);
  }

  @Test
  void log_when_wrong_line_numbers() throws IOException {
    SensorContextTester context = getSensorContext("wrong_lines_resultset.json", "file2.rb");
    sensor.execute(context);

    String expectedMessage = "Invalid coverage information on file: '/Absolute/Path/To/file2.rb'";
    assertThat(logTester.logs()).contains(expectedMessage);
  }

  @Test
  void log_when_invalid_format() throws IOException {
    SensorContextTester context = getSensorContext("invalid_resultset.json", "file1.rb");
    sensor.execute(context);

    String expectedMessage = String.format(
      "Cannot read coverage report file, expecting standard SimpleCov JSON formatter output: 'invalid_resultset.json'");
    assertThat(logTester.logs()).contains(expectedMessage);
  }

  @Test
  void log_when_unsupported_format_version_0_18() throws IOException {
    SensorContextTester context = getSensorContext("unsupported_resultset_0_18.json", "file1.rb");
    sensor.execute(context);

    String expectedWarning =
      "Importing SimpleCov resultset JSON will not be supported from simplecov 18.0. Consider using the JSON formatter, available from SimpleCov 20.0";
    assertThat(logTester.logs(Level.WARN)).contains(expectedWarning);

    String expectedError =
      "Cannot read coverage report file, expecting standard SimpleCov JSON formatter output: 'unsupported_resultset_0_18.json'";
    assertThat(logTester.logs(Level.ERROR)).contains(expectedError);
  }

  @Test
  void log_when_invalid_report_path() throws IOException {
    SensorContextTester context = getSensorContext("noFile.json", "file1.rb");
    sensor.execute(context);

    assertThat(logTester.logs()).contains("SimpleCov report not found: 'noFile.json'");
  }

  @Test
  void log_when_can_not_find_file_path() throws IOException {
    Configuration config = new MapSettings()
      .setProperty(RubyPlugin.EXCLUSIONS_KEY, RubyPlugin.EXCLUSIONS_DEFAULT_VALUE)
      .asConfig();
    SensorContextTester context = getSensorContext("resultset_missing_and_vendor_files.json", "file1.rb");

    sensor = new SimpleCovSensor(new RubyExclusionsFileFilter(config));
    sensor.execute(context);

    assertThat(logTester.logs())
      .hasSize(2)
      .containsExactly(
        "Importing SimpleCov resultset JSON will not be supported from simplecov 18.0. Consider using the JSON formatter, available from SimpleCov 20.0",
        "File '/Absolute/Path/To/missing_file.rb' is present in coverage report but cannot be found in filesystem");
  }

  @Test
  void success_for_report_present() throws IOException {
    SensorContextTester context = getSensorContext("noFile2.json,resultset_2.json", "file1.rb", "file2.rb");
    sensor.execute(context);

    assertThat(logTester.logs()).contains("SimpleCov report not found: 'noFile2.json'");

    assertThat(context.lineHits(MODULE_KEY + ":file1.rb", 9)).isEqualTo(1);
    assertThat(context.lineHits(MODULE_KEY + ":file2.rb", 1)).isEqualTo(3);
  }

  private SensorContextTester getSensorContext(String coverageReportPath, String... fileNames) throws IOException {
    Path baseDir = COVERAGE_DIR.toAbsolutePath();
    SensorContextTester context = SensorContextTester.create(baseDir);
    context.setSettings(new MapSettings());
    context.settings().setProperty("sonar.ruby.coverage.reportPaths", coverageReportPath);

    DefaultFileSystem defaultFileSystem = new DefaultFileSystem(new File(MODULE_KEY));
    createReportFiles(coverageReportPath, baseDir, defaultFileSystem);
    for (String fileName : fileNames) {
      DefaultInputFile inputFile = createInputFile(fileName, fileContent(baseDir, fileName));
      defaultFileSystem.add(inputFile);
    }

    context.setFileSystem(defaultFileSystem);
    return context;
  }

  private void createReportFiles(String coverageReportPath, Path baseDir, DefaultFileSystem defaultFileSystem) throws IOException {
    String[] coverageReportPaths = coverageReportPath.split(",");
    for (String reportPath : coverageReportPaths) {
      reportPath = reportPath.trim();
      // if report is relative path we create it under fake filesystem
      if (!Paths.get(coverageReportPath).isAbsolute()) {
        try {
          DefaultInputFile coverageFile = createInputFile(reportPath, fileContent(baseDir, reportPath));
          defaultFileSystem.add(coverageFile);
        } catch (NoSuchFileException e) {
          // tests can simulate non-existing file, this is OK
        }
      }
    }
  }


  private DefaultInputFile createInputFile(String fileName, String content) {
    return TestInputFileBuilder.create(MODULE_KEY, fileName)
      .setType(InputFile.Type.MAIN)
      .initMetadata(content)
      .setContents(content)
      .build();
  }

  private String fileContent(Path baseDir, String fileName) throws IOException {
    Path filePath = baseDir.resolve(fileName);
    return new String(Files.readAllBytes(filePath), UTF_8);
  }
}
