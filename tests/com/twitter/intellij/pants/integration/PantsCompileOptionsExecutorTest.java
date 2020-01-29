// Copyright 2016 Pants project contributors (see CONTRIBUTORS.md).
// Licensed under the Apache License, Version 2.0 (see LICENSE).

package com.twitter.intellij.pants.integration;

import com.twitter.intellij.pants.service.PantsCompileOptionsExecutor;
import com.twitter.intellij.pants.settings.PantsExecutionSettings;
import com.twitter.intellij.pants.testFramework.OSSPantsIntegrationTest;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

public class PantsCompileOptionsExecutorTest extends OSSPantsIntegrationTest {

  public void testProjectName() throws Throwable {
    String deepDir = new String(new char[100]).replace("\0", "dummy/");
    assertTrue(deepDir.length() > PantsCompileOptionsExecutor.PROJECT_NAME_LIMIT);

    PantsExecutionSettings settings = executionSettings(deepDir);

    PantsCompileOptionsExecutor executor = PantsCompileOptionsExecutor.create(
      getProjectFolder().getPath(),
      settings
    );

    String projectName = executor.getDefaultProjectName();
    assertNotContainsSubstring(projectName, File.separator);
    assertEquals(PantsCompileOptionsExecutor.PROJECT_NAME_LIMIT, projectName.length());
  }

  public void testRootLevelBuildFile() throws Throwable {
    Path rootLevelBuildFile = getProjectFolder().toPath().resolve("BUILD");
    PantsExecutionSettings settings = executionSettings(rootLevelBuildFile.toString());

    PantsCompileOptionsExecutor executor = PantsCompileOptionsExecutor.create(
      getProjectFolder().getPath(),
      settings
    );

    String projectName = executor.getDefaultProjectName();
    assertEquals(projectName, getProjectFolder().getName());
  }

  public void testRootDirectory() throws Throwable {
    PantsExecutionSettings settings = executionSettings(getProjectFolder().getPath(), "foo");

    PantsCompileOptionsExecutor executor = PantsCompileOptionsExecutor.create(
      getProjectFolder().getPath(),
      settings
    );

    String projectName = executor.getDefaultProjectName();
    assertEquals(projectName, getProjectFolder().getName());
  }

  public void testNonRootLevelBuildFile() throws Throwable {
    Path rootLevelBuildFile = getProjectFolder().toPath().resolve("foo/bar/baz").resolve("BUILD");
    PantsExecutionSettings settings = executionSettings(rootLevelBuildFile.toString(), "foo");

    PantsCompileOptionsExecutor executor = PantsCompileOptionsExecutor.create(
      getProjectFolder().getPath(),
      settings
    );

    String projectName = executor.getDefaultProjectName();
    assertEquals(projectName, "foo.bar.baz");
  }

  private PantsExecutionSettings executionSettings(String... projectPath) {
    return new PantsExecutionSettings(
      Arrays.asList(projectPath),
      false, // include libs and sources. does not matter here
      false, // use idea project jdk. does not matter here.
      false, // pants qexport dep as jar
      false, // incremental imports. does not matter here.
      false // use intellij compiler
    );
  }
}
