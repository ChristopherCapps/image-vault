package com.imagevault.core.git;

import com.imagevault.core.Command.CommandResult.CommandResultSupport;
import com.imagevault.core.Command.CommandResult.CommandSupport;
import com.imagevault.core.git.GitCommand.GitCommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.git.Git;
import java.util.LinkedList;
import java.util.List;

public abstract class GitCommand<TResult extends GitCommandResult> extends CommandSupport<TResult> {

  protected String command;
  protected List<String> arguments = new LinkedList<>();

  @Override
  protected List<String> build() {
    return arguments;
  }

  public abstract static class Builder<TCommand extends GitCommand<?>>
      extends CommandSupport.Builder<TCommand, Builder<TCommand>> {

    protected Builder() {
      withExecutable(Git.getExecutable());
    }

    public Builder<TCommand> withCommand(final String command) {
      return set(o -> o.command = command);
    }

    public Builder<TCommand> withArgument(final String argument) {
      return set(o -> o.arguments.add(argument));
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
