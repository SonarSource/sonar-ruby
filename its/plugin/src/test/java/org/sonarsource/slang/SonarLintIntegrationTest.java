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
package org.sonarsource.slang;

import com.sonar.orchestrator.config.Configuration;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.Locators;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesAndTrackParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis.AnalyzeFilesResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;
import org.sonarsource.sonarlint.core.test.utils.SonarLintBackendFixture;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import org.sonarsource.sonarlint.core.test.utils.plugins.Plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

public class SonarLintIntegrationTest {
  private static final String CONFIG_SCOPE_ID = "CONFIG_SCOPE_ID";

  private static final String FILE_CONTENTS_SONAR =
    """
    def fooBar()              # ruby:S100
      if true                 # ruby:S1145
        password = 'blabla'   # ruby:S1481
      end
    end
    """;

  private static final String FILE_CONTENTS_NOSONAR =
    """  
    def fooBar()             # NOSONAR
      if true                # ruby:S1145
        password = 'blabla'  # NOSONAR
      end
    end
    """;

  private static Path pluginPath;

  private SonarLintBackendFixture.FakeSonarLintRpcClient client;
  private SonarLintTestRpcServer backend;

  /**
   * Initialize {@link #pluginPath}:
   *  <li>In local environment, use the plugin built on local machine.
   *  <li>In QA environment in CI, the plugin is downloaded using Maven coordinates.
   */
  @BeforeAll
  static void initPluginLocation() {
    String slangVersion = System.getProperty("slangVersion");

    if (slangVersion != null && !slangVersion.isEmpty()) {
      Locators locators = new Locators(Configuration.createEnv());
      MavenLocation mavenLocation = MavenLocation.of("org.sonarsource.slang", "sonar-ruby-plugin", slangVersion);
      pluginPath = locators.locate(mavenLocation).toPath();
    } else {
      pluginPath = FileLocation.byWildcardMavenFilename(new File("../../sonar-ruby-plugin/build/libs"), "sonar-ruby-plugin-*-all.jar").getFile().toPath();
    }
  }

  @SonarLintTest
  public void test(SonarLintTestHarness harness, @TempDir Path baseDir) throws IOException {
    ClientFileDto fileDto = createFile(baseDir, "foo_sonar.rb", FILE_CONTENTS_SONAR);
    initWithFiles(harness, baseDir, fileDto);

    List<RaisedIssueDto> issues = analyzeFileAndGetIssues(fileDto.getUri());

    assertThat(issues)
      .extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactlyInAnyOrder(
        tuple("ruby:S100", new TextRangeDto(1, 4, 1, 10)),
        tuple("ruby:S1145", new TextRangeDto(2, 5, 2, 9)),
        tuple("ruby:S1481", new TextRangeDto(3, 4, 3, 12))
      );
  }

  @SonarLintTest
  public void test_nosonar(SonarLintTestHarness harness, @TempDir Path baseDir) throws IOException {
    ClientFileDto fileDto = createFile(baseDir, "foo_nosonar.rb", FILE_CONTENTS_NOSONAR);
    initWithFiles(harness, baseDir, fileDto);

    List<RaisedIssueDto> issues = analyzeFileAndGetIssues(fileDto.getUri());

    assertThat(issues)
      .extracting(RaisedIssueDto::getRuleKey, RaisedIssueDto::getTextRange)
      .usingRecursiveFieldByFieldElementComparator()
      .containsExactly(tuple("ruby:S1145", new TextRangeDto(2, 5, 2, 9)));

    fail("Intentional fail() to validate CI");
  }

  private List<RaisedIssueDto> analyzeFileAndGetIssues(URI fileUri) {
    UUID analysisId = UUID.randomUUID();
    AnalyzeFilesAndTrackParams params = new AnalyzeFilesAndTrackParams(CONFIG_SCOPE_ID, analysisId, List.of(fileUri), Map.of(), false);
    AnalyzeFilesResponse analysisResult =
      backend
        .getAnalysisService()
        .analyzeFilesAndTrack(params)
        .join();
    assertThat(analysisResult.getFailedAnalysisFiles()).isEmpty();
    await()
      .atMost(15, TimeUnit.SECONDS)
      .untilAsserted(() -> assertThat(client.getRaisedIssuesForScopeIdAsList(CONFIG_SCOPE_ID)).isNotEmpty());
    return client.getRaisedIssuesForScopeId(CONFIG_SCOPE_ID).get(fileUri);
  }

  private void initWithFiles(SonarLintTestHarness harness, Path baseDir, ClientFileDto fileDTOs) {
    client = harness
      .newFakeClient()
      .withInitialFs(CONFIG_SCOPE_ID, baseDir, List.of(fileDTOs))
      .build();

    backend = harness
      .newBackend()
      .withStandaloneEmbeddedPluginAndEnabledLanguage(new Plugin(Set.of(Language.RUBY), pluginPath, "", ""))
      .withUnboundConfigScope(CONFIG_SCOPE_ID)
      .start(client);
  }

  private static ClientFileDto createFile(Path folderPath, String fileName, String content) throws IOException {
    Path filePath = folderPath.resolve(fileName);
    Files.writeString(filePath, content);
    return new ClientFileDto(
      filePath.toUri(), folderPath.relativize(filePath), CONFIG_SCOPE_ID, false, null, filePath, null, Language.RUBY, true);
  }
}
