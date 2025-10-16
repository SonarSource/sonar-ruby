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
package org.sonarsource.slang;

import com.sonar.orchestrator.container.Edition;
import com.sonar.orchestrator.junit4.OrchestratorRule;
import com.sonar.orchestrator.junit4.OrchestratorRuleBuilder;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.locator.Location;
import com.sonar.orchestrator.locator.MavenLocation;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that verifies RubyProfileRegistrar functionality by using a separate
 * Orchestrator instance with both the Ruby plugin and a test plugin that registers a custom rule.
 */
public class ProfileRegistrarTest {

  @ClassRule
  public static final OrchestratorRule ORCHESTRATOR;

  static {
    OrchestratorRuleBuilder orchestratorBuilder = OrchestratorRule.builderEnv();
    addRubyPlugin(orchestratorBuilder);
    addTestPlugin(orchestratorBuilder);
    ORCHESTRATOR = orchestratorBuilder
      .useDefaultAdminCredentialsForBuilds(true)
      .setEdition(Edition.ENTERPRISE_LW)
      .activateLicense()
      .setSonarVersion(System.getProperty("sonar.runtimeVersion", "LATEST_RELEASE"))
      .build();
  }

  private static void addRubyPlugin(OrchestratorRuleBuilder builder) {
    String slangVersion = System.getProperty("slangVersion");

    Location pluginLocation;
    String plugin = "sonar-ruby-plugin";
    if (StringUtils.isEmpty(slangVersion)) {
      // use the plugin that was built on local machine
      pluginLocation = FileLocation.byWildcardMavenFilename(
        new File("../../" + plugin + "/build/libs"),
        plugin + "-*-all.jar");
    } else {
      // QA environment downloads the plugin built by the CI job
      pluginLocation = MavenLocation.of("org.sonarsource.slang", plugin, slangVersion);
    }

    builder.addPlugin(pluginLocation);
  }

  private static void addTestPlugin(OrchestratorRuleBuilder builder) {
    // Always use the test plugin that was built on local machine
    String testPlugin = "test-plugin";
    Location testPluginLocation = FileLocation.byWildcardMavenFilename(
      new File("../test-plugin/build/libs"),
      testPlugin + "-*.jar");
    builder.addPlugin(testPluginLocation);
  }

  private static WsClient newWsClient() {
    return WsClientFactories.getDefault().newClient(HttpConnector.newBuilder()
      .url(ORCHESTRATOR.getServer().getUrl())
      .build());
  }

  @Test
  public void testPluginRegistersRuleInDefaultRubyProfile() {
    var wsClient = newWsClient();
    
    // First, query the quality profiles to find the profile key for Ruby's "Sonar way" profile
    var profileSearchRequest = new org.sonarqube.ws.client.qualityprofiles.SearchRequest()
      .setLanguage("ruby");
    
    var profileResponse = wsClient.qualityprofiles().search(profileSearchRequest);
    
    var rubyProfile = profileResponse.getProfilesList().stream()
      .filter(profile -> "Sonar way".equals(profile.getName()) && profile.getIsBuiltIn())
      .findFirst()
      .orElseThrow(() -> new AssertionError("Built-in 'Sonar way' profile not found for Ruby language"));
    
    var profileKey = rubyProfile.getKey();
    
    // Query the active rules in the built-in "Sonar way" profile for Ruby language using the profile key
    var rulesSearchRequest = new org.sonarqube.ws.client.rules.SearchRequest()
      .setLanguages(List.of("ruby"))
      .setQprofile(profileKey)
      .setActivation("true");

    var rulesResponse = wsClient.rules().search(rulesSearchRequest);

    // Verify that the profile contains the TEST001 rule from ruby-test repository
    // This rule is registered by the TestProfileRegistrar in the test-plugin
    var testRules = rulesResponse.getRulesList().stream()
      .filter(rule -> "ruby-test:TEST001".equals(rule.getKey()))
      .collect(Collectors.toList());

    assertThat(testRules)
      .as("Rule ruby-test:TEST001 should be registered in the default Ruby profile by TestProfileRegistrar")
      .hasSize(1);

    var testRule = testRules.get(0);
    assertThat(testRule.getKey()).isEqualTo("ruby-test:TEST001");
    assertThat(testRule.getRepo()).isEqualTo("ruby-test");
  }
}

