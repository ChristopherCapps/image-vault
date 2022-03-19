package com.imagevault.core;

import com.imagevault.core.Command.CommandResult;
import com.imagevault.core.exiftool.VersionCommand;
import com.imagevault.core.exiftool.VersionCommand.VersionCommandResult;
import com.imagevault.io.Console;
import com.imagevault.io.Persistence.Logging;
import com.imagevault.io.exiftool.Exiftool;
import com.imagevault.io.git.Git;
import java.util.function.Function;

public class Engine {

  public static void initialize() {
    Logging.debugSection("blorple: 0.1");

    Logging.debug("exiftool version: '%s'",
        initializeTool(
            "exiftool",
            Exiftool.EXECUTABLE_ENV,
            VersionCommand.of(),
            VersionCommandResult::getVersion));

    Logging.debug("git version: '%s'",
        initializeTool(
            "git",
            Git.EXECUTABLE_ENV,
            com.imagevault.core.git.VersionCommand.of(),
            com.imagevault.core.git.VersionCommand.VersionCommandResult::getVersion));
  }

  private static <TResult extends CommandResult> String initializeTool(
      final String toolName,
      final String toolPathEnvVar,
      final Command<TResult> versionCommand,
      final Function<TResult, String> versionFn) {
    final TResult result = versionCommand.run();
    result.getThrowable()
        .ifPresent(t -> {
          Console.blorple("unable to find or execute %s, which is required", toolName);
          Console.abend(t,
              "\n\nYou may need to explicitly set the path to this executable in the "
                  + "environment variable '%s'.\n", toolPathEnvVar);
        });
    return versionFn.apply(result);
  }

}

// init
// import
// import --simulated
// export
