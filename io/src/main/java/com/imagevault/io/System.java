package com.imagevault.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.SystemUtils;

public class System {

  public static class Paths {

    public static Path getHomePath() {
      final String envHomeVar = SystemUtils.getEnvironmentVariable("PHOTO_VAULT_HOME",
          "user.dir");
      return Path.of(envHomeVar);
    }

    public static Path getExecutablePath() {
      return getHomePath().resolve("bin");
    }

    public static Path getConfigPath() {
      final String configName = SystemUtils.IS_OS_WINDOWS ? "PhotoVault" : ".photo_vault";
      return getHomePath().resolve(configName);
    }

    public static Path getPathForFile(final String filepath) {
      return (new File(filepath)).toPath();
    }

    public static Path getAbsolutePathForFile(final String filepath) {
      return getPathForFile(filepath).toAbsolutePath();
    }

    public static Path getAbsolutePathForExistingFile(final String filepath) {
      final Path path = getAbsolutePathForFile(filepath);
      if (Files.exists(path)) {
        return path;
      } else {
        throw new RuntimeException(
            String.format("File '%s' is required but does not exist", path));
      }
    }
  }

  public static class Environment {

    public static String getVariable(final String name, final String defaultValue) {
      return SystemUtils.getEnvironmentVariable(name, defaultValue);
    }
  }

  public static Platform getPlatform() {
    if (SystemUtils.IS_OS_MAC) {
      return Platform.MACOS;
    } else if (SystemUtils.IS_OS_LINUX) {
      return Platform.LINUX;
    } else if (SystemUtils.IS_OS_WINDOWS) {
      return Platform.WINDOWS;
    } else {
      throw new RuntimeException("Unrecognized platform: " + SystemUtils.OS_NAME);
    }
  }

  public static enum Platform {
    LINUX,
    MACOS,
    WINDOWS;
  }

}
