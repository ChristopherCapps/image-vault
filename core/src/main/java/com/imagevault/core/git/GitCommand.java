package com.imagevault.core.git;

import com.imagevault.core.Command.CommandResult.CommandResultSupport;
import com.imagevault.core.Command.CommandSupport;
import com.imagevault.core.git.GitCommand.GitCommandResult;
import com.imagevault.io.git.Git;
import java.util.LinkedList;
import java.util.List;

public abstract class GitCommand<TResult extends GitCommandResult> extends CommandSupport<TResult> {

  public abstract static class Builder<TCommand extends GitCommand<?>>
      extends CommandSupport.Builder<TCommand, Builder<TCommand>> {

    private List<String> flags = new LinkedList<>();
    private List<String> parameterizedFlags = new LinkedList<>();
    private List<String> optionValues = new LinkedList<>();
    private String command;
    private List<String> commandFlags = new LinkedList<>();
    private List<String> parameterizedCommandFlags = new LinkedList<>();
    private List<String> commandArguments = new LinkedList<>();

    protected Builder() {
      withExecutable(Git.getExecutable());
    }

    public Builder<TCommand> withFlag(final String flag) {
      // --version
      flags.add(flag);
      return this;
    }

    public Builder<TCommand> withParameterizedFlag(final String flag, final String parameter) {
      // --namespace=<name>
      parameterizedFlags.add(String.format("%s=%s", flag, parameter));
      return this;
    }

    public Builder<TCommand> withOptionValue(final String option, final String value) {
      // -C <path>
      optionValues.add(String.format("%s %s", option, value));
      return this;
    }

    public Builder<TCommand> withCommand(final String command) {
      if (this.command != null) {
        throw new IllegalArgumentException(
            "git permits only one command per invocation and '" + command + "' already set");
      }
      this.command = command;
      return this;
    }

    public Builder<TCommand> withCommandFlag(final String flag) {
      // --version
      commandFlags.add(flag);
      return this;
    }

    public Builder<TCommand> withParameterizedCommandFlag(final String flag,
        final String parameter) {
      // --namespace=<name>
      parameterizedCommandFlags.add(String.format("%s=%s", flag, parameter));
      return this;
    }

    public Builder<TCommand> withCommandArgument(final String argument) {
      commandArguments.add(argument);
      return this;
    }

    @Override
    protected void buildArguments() {
      super.buildArguments();
      flags.forEach(this::withArgument);
      parameterizedFlags.forEach(this::withArgument);
      withArgument(command);
      commandFlags.forEach(this::withArgument);
      parameterizedCommandFlags.forEach(this::withArgument);
      commandArguments.forEach(this::withArgument);
    }
  }

  public interface GitCommandResult extends CommandResult {

  }

  public static class GitCommandResultSupport extends CommandResultSupport implements
      GitCommandResult {

    protected GitCommandResultSupport(final ProcessResult processResult) {
      super(processResult);
    }
  }

}
