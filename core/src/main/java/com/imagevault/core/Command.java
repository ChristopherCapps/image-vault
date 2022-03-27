package com.imagevault.core;

import com.imagevault.core.Command.CommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.Process.ProcessResultSupport;
import org.apache.commons.exec.CommandLine;

public interface Command<TResult extends CommandResult> {

  TResult run();

  interface CommandResult extends ProcessResult {

    class CommandResultSupport extends ProcessResultSupport implements CommandResult {

      protected CommandResultSupport(final ProcessResult processResult) {
        super(processResult);
      }

      protected CommandResultSupport(
          final CommandLine commandLine,
          final int exitCode,
          final String output,
          final String error,
          final Throwable throwable) {
        super(commandLine, exitCode, output, error, throwable);
      }
    }
  }

  abstract class CommandSupport<TResult extends CommandResult> extends
      com.imagevault.io.Process implements Command<TResult> {

    protected CommandSupport() {
      // builder only
    }

    @Override
    public TResult run() {
      return buildCommandResult(super.run());
    }

    protected abstract TResult buildCommandResult(final ProcessResult processResult);

    public abstract static class Builder<TCommand extends CommandSupport<?>,
        TBuilder extends Builder<TCommand, TBuilder>>
        extends com.imagevault.io.Process.Builder<TCommand> {

      protected void buildArguments() {

      }

      @Override
      public TCommand build() {
        buildArguments();
        return super.build();
      }
    }
  }
}