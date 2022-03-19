package com.imagevault.io;

import static com.imagevault.io.Persistence.Logging;
import static java.util.Objects.requireNonNull;

import com.imagevault.common.ValidatingBuilder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class Process {

  @NotNull
  protected Path executable;
  @NotNull
  protected List<String> arguments = new LinkedList<>();

  protected Process() {
    // builder only
  }

  public Path getExecutable() {
    return executable;
  }

  public List<String> getArguments() {
    return UnmodifiableList.unmodifiableList(arguments);
  }

  public ProcessResult run() {
    final CommandLine commandLine = new CommandLine(executable.toString());
    arguments.stream().forEach(commandLine::addArgument);

    final ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    final ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();

    DefaultExecutor executor = new DefaultExecutor();
    executor.setExitValues(null); // don't interpret exit values
    executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
    executor.setWorkingDirectory(SystemUtils.getUserDir());
    executor.setStreamHandler(new PumpStreamHandler(outputBuffer, errorBuffer));

    try {
      Logging.debug("executing: %s", commandLine);
      final int exitValue = executor.execute(commandLine);
      final ProcessResult processResult = ProcessResultSupport.of(
          commandLine,
          exitValue,
          convertBufferToString(outputBuffer),
          convertBufferToString(errorBuffer));
      Logging.debug("result: %s", processResult);
      return processResult;
    } catch (Exception e) {
      Logging.error(e, "failed to execute: %s", commandLine.getExecutable());
      return ProcessResultSupport.of(commandLine, e);
    }
  }

  public static ProcessResult run(final Path executable, final List<String> arguments) {
    final Builder builder = newBuilder()
        .withExecutable(requireNonNull(executable));
    requireNonNull(arguments)
        .forEach(arg -> builder.withArgument(arg));
    return builder.build().run();
  }

  private static String convertBufferToString(final ByteArrayOutputStream buffer) {
    return StringUtils.trimToEmpty(new String(buffer.toByteArray(), Charset.defaultCharset()));
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public interface ProcessResult {

    CommandLine getCommandLine();

    Optional<String> getOutput();

    Optional<String> getError();

    Optional<Throwable> getThrowable();

    Optional<Integer> getExitCode();
  }

  public static class ProcessResultSupport implements ProcessResult {

    private final CommandLine commandLine;
    private final int exitCode;
    private final String output;
    private final String error;
    private final Throwable throwable;

    public static ProcessResultSupport of(final CommandLine commandLine,
        final Throwable throwable) {
      return new ProcessResultSupport(commandLine, -1, null, null, throwable);
    }

    public static ProcessResultSupport of(
        final CommandLine commandLine,
        final int exitCode,
        final String output,
        final String error) {
      return new ProcessResultSupport(commandLine, exitCode, output, error, null);
    }

    protected ProcessResultSupport(final ProcessResult processResult) {
      this.commandLine = processResult.getCommandLine();
      this.exitCode = processResult.getExitCode().orElse(-1);
      this.output = processResult.getOutput().orElse(null);
      this.error = processResult.getError().orElse(null);
      this.throwable = processResult.getThrowable().orElse(null);
    }

    protected ProcessResultSupport(
        final CommandLine commandLine,
        final int exitCode,
        final String output,
        final String error,
        final Throwable throwable) {
      this.commandLine = requireNonNull(commandLine);
      this.exitCode = exitCode;
      this.output = output;
      this.error = error;
      this.throwable = throwable;
    }

    @Override
    public Optional<String> getOutput() {
      return Optional.ofNullable(output);
    }

    @Override
    public Optional<String> getError() {
      return Optional.ofNullable(error);
    }

    @Override
    public Optional<Integer> getExitCode() {
      return Optional.ofNullable(exitCode);
    }

    @Override
    public CommandLine getCommandLine() {
      return commandLine;
    }

    @Override
    public Optional<Throwable> getThrowable() {
      return Optional.ofNullable(throwable);
    }

    @Override
    public String toString() {
      return "ProcessResultSupport{" +
          "commandLine=" + commandLine +
          ", exitCode=" + exitCode +
          ", output='" + output + '\'' +
          ", error='" + error + '\'' +
          ", throwable=" + throwable +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ProcessResultSupport that = (ProcessResultSupport) o;
      return exitCode == that.exitCode && Objects.equals(commandLine, that.commandLine)
          && Objects.equals(output, that.output) && Objects.equals(error,
          that.error) && Objects.equals(throwable, that.throwable);
    }

    @Override
    public int hashCode() {
      return Objects.hash(commandLine, exitCode, output, error, throwable);
    }

    protected String getRequiredOutput() {
      return getRequiredString(getOutput());
    }

    protected RuntimeException internalErrorInOutput() {
      return internalError("internal error: unexpected command output for %s: %s", getCommandLine(),
          getOutput());
    }

    protected RuntimeException internalError(final String format, final Object... args) {
      return Console.abend(format, args);
    }

    protected String getRequiredString(final Optional<String> stringOptional) {
      return stringOptional
          .filter(StringUtils::isNotBlank)
          .map(StringUtils::trim)
          .orElseThrow(() -> internalError(
              "internal error: command %s did not produce expected output\nSee log '%s' for details.",
              getCommandLine(),
              Logging.getLogPath()));
    }
  }

  public static class Builder extends ValidatingBuilder<Process, Builder> {

    public Builder withExecutable(final Path executable) {
      return set(o -> o.executable = executable);
    }

    public Builder withArgument(final String argument) {
      return set(o -> o.arguments.add(argument));
    }

    @Override
    protected Process newObject() {
      return new Process();
    }
  }

}
