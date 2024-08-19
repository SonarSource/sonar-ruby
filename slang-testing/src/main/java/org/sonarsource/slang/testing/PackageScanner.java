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
package org.sonarsource.slang.testing;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PackageScanner {

  private PackageScanner() {
    // static usage only
  }

  /**
   * Returns the fully qualified names (FQNs) of the classes inside @packageName implementing SlangCheck.
   * @param packageName Used to filter classes - the FQN of a class contains the package name.
   * @return A list of slang checks (FQNs).
   */
  public static List<String> findSlangChecksInPackage(String packageName) {
    try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packageName).scan()) {
      Map<String, ClassInfo> allClasses = scanResult.getAllClassesAsMap();
      List<String> testClassesInPackage = new ArrayList<>();
      for (Map.Entry<String, ClassInfo> classInfoEntry : allClasses.entrySet()) {
        String name = classInfoEntry.getKey();
        ClassInfo classInfo = classInfoEntry.getValue();
        if (name.startsWith(packageName) && classInfo.getInterfaces().stream().anyMatch(i -> i.getSimpleName().equals("SlangCheck"))) {
          testClassesInPackage.add(classInfo.getName());
        }
      }
      return testClassesInPackage;
    }
  }
}
