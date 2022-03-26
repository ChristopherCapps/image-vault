package com.imagevault.core;

import com.imagevault.core.Command.CommandResult;
import com.imagevault.core.exiftool.VersionCommand;
import com.imagevault.core.exiftool.VersionCommand.VersionCommandResult;
import com.imagevault.core.git.ConfigCommand;
import com.imagevault.core.git.InitializeRepositoryCommand;
import com.imagevault.core.git.RepositoryRootPathCommand;
import com.imagevault.io.Console;
import com.imagevault.io.Persistence.Logging;
import com.imagevault.io.Shell;
import com.imagevault.io.exiftool.Exiftool;
import com.imagevault.io.git.Git;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

public class Engine {

  public static Engine of() {
    return new Engine();
  }

  private final String exiftoolVersion;
  private final String gitVersion;
  private final Path workingPath;

  private Engine() {
    Logging.debugSection("blorple: 0.1");

    this.workingPath = Shell.Paths.getWorkingDirectoryPath();
    Logging.debug("working path: %s", workingPath);

    this.exiftoolVersion = initializeTool(
        "exiftool",
        Exiftool.EXECUTABLE_ENV,
        VersionCommand.of(),
        VersionCommandResult::getVersion);
    Logging.debug("exiftool version: '%s'", exiftoolVersion);

    this.gitVersion = initializeTool(
        "git",
        Git.EXECUTABLE_ENV,
        com.imagevault.core.git.VersionCommand.of(),
        com.imagevault.core.git.VersionCommand.VersionCommandResult::getVersion);
    Logging.debug("git version: '%s'", gitVersion);
  }

  private Optional<Path> getRepositoryPath() {
    Optional<Path> repositoryPath = RepositoryRootPathCommand.of().run().getRepositoryRootPath();
    Logging.debug("git repository: '%s'", repositoryPath);
    return repositoryPath;
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

  public void init() {
    // First, make sure the working directory is not already part of a repo
    getRepositoryPath()
        .ifPresent(
            path -> Console.abend("current directory already belongs to a repository at root: %s",
                path));

    // Create a new repo with branch "master"
    final Path repositoryGitPath = InitializeRepositoryCommand.of().run().getRepositoryGitPath();
    // Lookup/validate the new repo path
    final Path repositoryPath = getRepositoryPath().orElseThrow(
        () -> Console.abend("unable to determine new repository path"));
    Logging.debug("created new repository at: %s", repositoryPath);
    Logging.debug("created git storage for repository at: %s", repositoryGitPath);

    // Configure the default user/email for the repo
    ConfigCommand.setUser(ConfigCommand.DEFAULT_USER).run();
    ConfigCommand.setEmail(ConfigCommand.DEFAULT_EMAIL).run();

    // Add existing files to the repo

    // Commit these files

    Console.blorple("initialized new repository: %s", repositoryPath);
  }
}

