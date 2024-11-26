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
package org.sonarsource.slang;

import com.sonar.orchestrator.build.SonarScanner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonarqube.ws.Issues.Issue;
import org.sonarqube.ws.client.issues.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;
public class ExternalReportTest extends TestBase {

  private static final String BASE_DIRECTORY = "projects/externalreport/";

  @Rule
  public TemporaryFolder tmpDir = new TemporaryFolder();

  @Test
  public void rubocop() {
    final String projectKey = "rubocop";
    SonarScanner sonarScanner = getSonarScanner(projectKey, BASE_DIRECTORY, "rubocop");
    sonarScanner.setProperty("sonar.ruby.rubocop.reportPaths", "rubocop-report.json");
    ORCHESTRATOR.executeBuild(sonarScanner);
    List<Issue> issues = getExternalIssues(projectKey);
    issues.sort(Comparator.comparing(Issue::getLine).thenComparing(Issue::getRule));

    assertThat(issues).hasSize(3);

    assertThat(issues.get(0).getRule()).isEqualTo("external_rubocop:Naming/FileName");
    assertThat(issues.get(0).getLine()).isEqualTo(1);
    assertThat(issues.get(0).getMessage()).isEqualTo("The name of this source file (`yaml-issue.rb`) should use snake_case.");
    assertThat(issues.get(0).getSeverity().name()).isEqualTo("INFO");
    assertThat(issues.get(0).getDebt()).isEqualTo("5min");

    assertThat(issues.get(1).getRule()).isEqualTo("external_rubocop:Style/FrozenStringLiteralComment");
    assertThat(issues.get(1).getLine()).isEqualTo(1);
    assertThat(issues.get(1).getMessage()).isEqualTo("Missing frozen string literal comment.");
    assertThat(issues.get(1).getSeverity().name()).isEqualTo("MINOR");
    assertThat(issues.get(1).getDebt()).isEqualTo("5min");

    assertThat(issues.get(2).getRule()).isEqualTo("external_rubocop:Security/YAMLLoad");
    assertThat(issues.get(2).getLine()).isEqualTo(2);
    assertThat(issues.get(2).getMessage()).isEqualTo("Prefer using `YAML.safe_load` over `YAML.load`.");
    assertThat(issues.get(2).getSeverity().name()).isEqualTo("MAJOR");
    assertThat(issues.get(2).getDebt()).isEqualTo("5min");
  }

  private List<Issue> getExternalIssues(String componentKey) {
    return newWsClient().issues().search(new SearchRequest().setComponentKeys(Collections.singletonList(componentKey)))
      .getIssuesList().stream()
      .filter(issue -> issue.getRule().startsWith("external_"))
      .collect(Collectors.toList());
  }

  private static String filePath(Issue issue) {
    return issue.getComponent().substring(issue.getComponent().indexOf(':') + 1);
  }

}
