package com.imagevault.core.git;

import com.imagevault.core.git.ConfigCommand.ConfigCommandResult;
import com.imagevault.io.Console;
import com.imagevault.io.Persistence.Logging;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.git.Git;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigCommand extends GitCommand<ConfigCommandResult> {

  public static final String DEFAULT_USER = "blorple";
  public static final String DEFAULT_EMAIL = "blorple@gmail.com";

  public static ConfigCommand setUser(final String user) {
    return newBuilder()
        .withCommand(Git.COMMAND_CONFIG,
            Pair.of(Git.COMMAND_CONFIG_USER_ARG, user))
        .build();
  }

  public static ConfigCommand setEmail(final String email) {
    return newBuilder()
        .withCommand(Git.COMMAND_CONFIG,
            Pair.of(Git.COMMAND_CONFIG_EMAIL_ARG, email))
        .build();
  }

  @Override
  protected ConfigCommandResult buildCommandResult(
      final ProcessResult processResult) {
    return new ConfigCommandResult(processResult);
  }

  private ConfigCommand() {
  }

  @Override
  public ConfigCommandResult run() {
    final ConfigCommandResult result = super.run();
    if (result.hasErrorExitCode()) {
      Console.abend("unable to configure repository: %s",
          result.getError().orElse(StringUtils.EMPTY));
    } else {
      Logging.debug("git: configuration succeeded: %s", result.getCommandLine());
    }
    return result;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends GitCommand.Builder<ConfigCommand> {

    private Builder() {
      // factory method only
    }

    @Override
    protected ConfigCommand newObject() {
      return new ConfigCommand();
    }
  }

  public static class ConfigCommandResult extends GitCommandResultSupport {

    protected ConfigCommandResult(
        ProcessResult processResult) {
      super(processResult);
    }

  }
}