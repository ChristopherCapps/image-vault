package com.imagevault.io.git;

import com.imagevault.io.Persistence.Logging;
import com.imagevault.io.Shell;
import com.imagevault.io.Shell.Platform;
import java.nio.file.Path;
import java.util.Locale;

public class Git {

  public final static String EXECUTABLE_ENV = "GIT_EXECUTABLE";

  public final static String EXECUTABLE_MAC = "git";
  public final static String EXECUTABLE_LINUX = "git";
  public final static String EXECUTABLE_WIN = "git.exe";

  public final static String OPTION_VERSION = "--version";

  public static Path getExecutable() {
    final Platform platform = Shell.getPlatform();
    Logging.debug("platform: %s", platform.name().toLowerCase(Locale.ROOT));
    final String defaultExecutable = switch (platform) {
      case LINUX -> EXECUTABLE_LINUX;
      case MACOS -> EXECUTABLE_MAC;
      case WINDOWS -> EXECUTABLE_WIN;
    };
    final Path selectedPath = Shell.Paths.getPathForFile(
        Shell.Environment.getVariable(EXECUTABLE_ENV,
            defaultExecutable));
    Logging.debug("git: %s", selectedPath);
    return selectedPath;

  }

}
