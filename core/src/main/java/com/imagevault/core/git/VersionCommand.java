package com.imagevault.core.git;

import com.imagevault.core.git.VersionCommand.VersionCommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.git.Git;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class VersionCommand extends GitCommand<VersionCommandResult> {

  public static VersionCommand of() {
    return VersionCommand.newBuilder()
        .withOption(Git.OPTION_VERSION)
        .build();
  }

  private VersionCommand() {
    // builder only
  }

  protected VersionCommandResult buildCommandResult(final ProcessResult processResult) {
    return new VersionCommandResult(processResult);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends GitCommand.Builder<VersionCommand> {

    private Builder() {
      // factory method only
    }

    @Override
    protected VersionCommand newObject() {
      return new VersionCommand();
    }
  }

  public static class VersionCommandResult extends GitCommandResultSupport {

    protected VersionCommandResult(final ProcessResult processResult) {
      super(processResult);
    }

    public String getVersion() {
      // format of version result is "git version <version>"
      return Optional.of(getRequiredOutput())
          .map(StringUtils::split)
          .filter(result -> result != null && result.length == 3)
          .map(result -> result[2])
          .orElseThrow(this::internalErrorInResult);
    }
  }
}