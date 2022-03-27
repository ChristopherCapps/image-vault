package com.imagevault.core.git;

import com.imagevault.core.git.InitializeRepositoryCommand.InitializeRepositoryCommandResult;
import com.imagevault.io.Console;
import com.imagevault.io.Shell;
import com.imagevault.io.git.Git;
import java.nio.file.Path;
import org.apache.commons.lang3.StringUtils;

public class InitializeRepositoryCommand extends GitCommand<InitializeRepositoryCommandResult> {

  public static final String DEFAULT_INITIAL_BRANCH = "master";

  public static InitializeRepositoryCommand of() {
    return of(DEFAULT_INITIAL_BRANCH);
  }

  public static InitializeRepositoryCommand of(final String initialBranchName) {
    return newBuilder()
        .withCommand(Git.COMMAND_INIT)
        .withParameterizedCommandFlag(Git.COMMAND_INIT_INITIAL_BRANCH_FLAG, initialBranchName)
        .build();
  }

  @Override
  protected InitializeRepositoryCommandResult buildCommandResult(
      final ProcessResult processResult) {
    return new InitializeRepositoryCommandResult(processResult);
  }

  private InitializeRepositoryCommand() {
  }

  @Override
  public InitializeRepositoryCommandResult run() {
    final InitializeRepositoryCommandResult result = super.run();
    if (result.hasErrorExitCode()) {
      Console.abend("unable to initialize the new repository: %s",
          result.getError().orElse(StringUtils.EMPTY));
    }
    return result;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends GitCommand.Builder<InitializeRepositoryCommand> {

    private Builder() {
      // factory method only
    }

    public GitCommand.Builder withInitialBranch(final String branch) {
      return withParameterizedCommandFlag(Git.COMMAND_INIT_INITIAL_BRANCH_FLAG, branch);
    }

    @Override
    protected InitializeRepositoryCommand newObject() {
      return new InitializeRepositoryCommand();
    }
  }

  public static class InitializeRepositoryCommandResult extends GitCommandResultSupport {

    protected InitializeRepositoryCommandResult(
        ProcessResult processResult) {
      super(processResult);
    }

    public Path getRepositoryGitPath() {
      final String successfulOutput = "Initialized empty Git repository in";
      final String gitPath = findOutputLineContainingRequired(successfulOutput).substring(
          successfulOutput.length()).trim();
      return Shell.Paths.getPathForFile(gitPath);
    }
  }
}