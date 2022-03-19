package com.imagevault.core;

import com.imagevault.common.ValidatingBuilder;
import com.imagevault.core.Command.CommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.Process.ProcessResultSupport;
import java.nio.file.Path;
import java.util.List;
import javax.validation.constraints.NotNull;
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

    abstract class CommandSupport<TResult extends CommandResult> implements Command<TResult> {

      @NotNull
      protected Path executable;

      protected CommandSupport() {
        // builder only
      }

      @Override
      public TResult run() {
        return buildCommandResult(com.imagevault.io.Process.run(executable, buildArguments()));
      }

      protected abstract List<String> buildArguments();

      protected abstract TResult buildCommandResult(final ProcessResult processResult);

      public abstract static class Builder<TCommand extends CommandSupport<?>,
          TBuilder extends Builder<TCommand, TBuilder>> extends
          ValidatingBuilder<TCommand, TBuilder> {

        public Builder<TCommand, TBuilder> withExecutable(final Path executable) {
          return set(o -> o.executable = executable);
        }
      }
    }
  }
}