/*
 * SonarSource Ruby
 * Copyright (C) 2018-2024 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.slang;

import java.util.List;
import org.junit.Test;
import org.sonarqube.ws.Issues;

import static org.assertj.core.api.Assertions.assertThat;

public class MeasuresTest extends TestBase {

  private static final String BASE_DIRECTORY = "projects/measures/";

  @Test
  public void ruby_measures() {
    final String projectKey = "rubyMeasures";
    ORCHESTRATOR.executeBuild(getSonarScanner(projectKey, BASE_DIRECTORY, "ruby"));

    final String componentKey = projectKey + ":file.rb";
    assertThat(getMeasureAsInt(projectKey, "files")).isEqualTo(2);
    assertThat(getMeasureAsInt(componentKey, "ncloc")).isEqualTo(8);
    assertThat(getMeasureAsInt(componentKey, "comment_lines")).isEqualTo(6);
    assertThat(getMeasureAsInt(componentKey, "statements")).isEqualTo(5);
    assertThat(getMeasureAsInt(componentKey, "cognitive_complexity")).isEqualTo(0);
    assertThat(getMeasure(componentKey, "ncloc_data").getValue()).isEqualTo("16=1;2=1;3=1;20=1;6=1;7=1;14=1;15=1");
    assertThat(getMeasure(componentKey, "executable_lines_data").getValue()).isEqualTo("3=1;20=1;7=1;14=1;15=1");

    List<Issues.Issue> issuesForRule = getIssuesForRule(projectKey, "ruby:S1135");
    assertThat(issuesForRule).extracting(Issues.Issue::getLine).containsExactly(18);
    assertThat(issuesForRule).extracting(Issues.Issue::getComponent).containsExactly(componentKey);
  }

}
