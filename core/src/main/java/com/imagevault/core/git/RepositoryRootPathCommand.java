package com.imagevault.core.git;

import com.imagevault.core.git.RepositoryRootPathCommand.RepositoryRootPathCommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.Shell;
import com.imagevault.io.git.Git;
import java.nio.file.Path;
import java.util.Optional;

public class RepositoryRootPathCommand extends GitCommand<RepositoryRootPathCommandResult> {

  public static RepositoryRootPathCommand of() {
    return newBuilder()
        .withArgument(Git.COMMAND_REV_PARSE)
        .withArgument(Git.COMMAND_REV_PARSE_SHOW_TOPLEVEL_ARG)
        .build();
  }

  @Override
  protected RepositoryRootPathCommandResult buildCommandResult(final ProcessResult processResult) {
    return new RepositoryRootPathCommandResult(processResult);
  }

  private RepositoryRootPathCommand() {
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends GitCommand.Builder<RepositoryRootPathCommand> {

    private Builder() {
      // factory method only
    }

    @Override
    protected RepositoryRootPathCommand newObject() {
      return new RepositoryRootPathCommand();
    }
  }

  public static class RepositoryRootPathCommandResult extends GitCommandResultSupport {

    protected RepositoryRootPathCommandResult(
        ProcessResult processResult) {
      super(processResult);
    }

    public Optional<Path> getRepositoryRootPath() {
      return
          errorContains("not a git repository") ?
              Optional.empty() :
              Optional.of(Shell.Paths.getPathForFile(getRequiredOutput()));
    }
  }
}