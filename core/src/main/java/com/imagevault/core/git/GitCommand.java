package com.imagevault.core.git;

import com.imagevault.core.Command.CommandResult.CommandResultSupport;
import com.imagevault.core.Command.CommandResult.CommandSupport;
import com.imagevault.core.git.GitCommand.GitCommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.git.Git;
import java.util.LinkedList;
import java.util.List;

public abstract class GitCommand<TResult extends GitCommandResult> extends CommandSupport<TResult> {

  protected List<String> options = new LinkedList<>();

  @Override
  protected List<String> buildArguments() {
    final List<String> arguments = new LinkedList<>();

    options.forEach(arguments::add);

    return arguments;
  }

  public abstract static class Builder<TCommand extends GitCommand<?>>
      extends CommandSupport.Builder<TCommand, Builder<TCommand>> {

    protected Builder() {
      withExecutable(Git.getExecutable());
    }

    public Builder<TCommand> withOption(final String option) {
      return set(o -> o.options.add(option));
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
