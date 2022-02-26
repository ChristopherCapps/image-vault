package com.imagevault.io;

import static java.util.Objects.requireNonNull;

import com.imagevault.common.ValidatingBuilder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Process {

  private static final Logger logger = LoggerFactory.getLogger(Process.class);

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
      logger.debug("Executing: '{}'", commandLine);
      final int exitValue = executor.execute(commandLine);
      final ProcessResult processResult = ProcessResultSupport.of(
          commandLine,
          exitValue,
          convertBufferToString(outputBuffer),
          convertBufferToString(errorBuffer));
      logger.debug("Execution of '{}' yielded result: {}", commandLine, processResult);
      return processResult;
    } catch (
        Exception e) {
      throw new RuntimeException(String.format("Failed to execute command: %s", commandLine), e);
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

    String getOutput();

    String getError();

    int getExitCode();
  }

  public static class ProcessResultSupport implements ProcessResult {

    private final CommandLine commandLine;
    private final int exitCode;
    private final String output;
    private final String error;

    public static ProcessResultSupport of(
        final CommandLine commandLine,
        final int exitCode,
        final String output,
        final String error) {
      return new ProcessResultSupport(commandLine, exitCode, output, error);
    }

    protected ProcessResultSupport(
        final CommandLine commandLine,
        final int exitCode,
        final String output,
        final String error) {
      this.commandLine = requireNonNull(commandLine);
      this.exitCode = exitCode;
      this.output = requireNonNull(output);
      this.error = requireNonNull(error);
    }

    @Override
    public String getOutput() {
      return output;
    }

    @Override
    public String getError() {
      return error;
    }

    @Override
    public int getExitCode() {
      return exitCode;
    }

    @Override
    public CommandLine getCommandLine() {
      return commandLine;
    }

    @Override
    public String toString() {
      return "ProcessResultSupport{" +
          "commandLine='" + commandLine + '\'' +
          ", exitCode=" + exitCode +
          ", output='" + output + '\'' +
          ", error='" + error + '\'' +
          '}';
    }

    public CommandLine commandLine() {
      return commandLine;
    }

    public int exitCode() {
      return exitCode;
    }

    public String output() {
      return output;
    }

    public String error() {
      return error;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this)
        return true;
      if (obj == null || obj.getClass() != this.getClass())
        return false;
      var that = (ProcessResultSupport) obj;
      return Objects.equals(this.commandLine, that.commandLine) &&
          this.exitCode == that.exitCode &&
          Objects.equals(this.output, that.output) &&
          Objects.equals(this.error, that.error);
    }

    @Override
    public int hashCode() {
      return Objects.hash(commandLine, exitCode, output, error);
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