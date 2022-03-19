package com.imagevault.core.exiftool;

import com.imagevault.core.Command.CommandResult.CommandResultSupport;
import com.imagevault.core.Command.CommandResult.CommandSupport;
import com.imagevault.core.exiftool.ExiftoolCommand.ExiftoolCommandResult;
import com.imagevault.io.Process.ProcessResult;
import com.imagevault.io.exiftool.Exiftool;
import com.imagevault.io.exiftool.Exiftool.Tag;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.validation.constraints.NotNull;

public abstract class ExiftoolCommand<TResult extends ExiftoolCommandResult> extends
    CommandSupport<TResult> {

  @NotNull
  protected List<String> tags = new LinkedList<>();
  protected List<String> options = new LinkedList<>();
  protected List<Path> paths = new LinkedList<>();

  protected ExiftoolCommand() {
  }

  @Override
  protected abstract TResult buildCommandResult(final ProcessResult processResult);

  @Override
  protected List<String> buildArguments() {
    final List<String> arguments = new LinkedList<>();

    options.forEach(arguments::add);
    tags.stream().forEach(tag -> addTag(tag, arguments));
    paths.stream().map(Path::toAbsolutePath).map(Path::toString).forEach(arguments::add);

    return arguments;
  }

  private void addTag(final String tag, final List<String> arguments) {
    arguments.add(Exiftool.OPTION_TAG_EXTRACT + tag);
  }

  public abstract static class Builder<TCommand extends ExiftoolCommand<?>>
      extends CommandSupport.Builder<TCommand, Builder<TCommand>> {

    protected Builder() {
      withExecutable(Exiftool.getExecutable());
    }

    public Builder<TCommand> withOption(final String option) {
      return set(o -> o.options.add(option));
    }

    public Builder<TCommand> withIoJsonOutputFormat() {
      return set(o -> o.options.add(Exiftool.OPTION_IO_JSON_OUTPUT_FORMAT));
    }

    public Builder<TCommand> withIoVeryShortOutputFormat() {
      return set(o -> o.options.add(Exiftool.OPTION_IO_VERY_SHORT_OUTPUT_FORMAT));
    }

    public Builder<TCommand> withProcessingExtension(final String extension) {
      return set(o -> o.options.add(withOption(Exiftool.OPTION_PROCESSING_EXTENSION, extension)));
    }

    public Builder<TCommand> withExtractTag(final Tag tag) {
      return withExtractTag(tag.name());
    }

    public Builder<TCommand> withExtractTags(final Tag... tag) {
      Arrays.stream(tag).sequential()
          .forEach(this::withExtractTag);
      return this;
    }

    public Builder<TCommand> withExtractTag(final String tag) {
      return set(o -> o.tags.add(tag));
    }

    public Builder<TCommand> withPath(final Path path) {
      return set(o -> o.paths.add(path));
    }

    private String withOption(final String option, final String value) {
      return String.format("%s %s", option, value);
    }
  }

  public interface ExiftoolCommandResult extends CommandResult {

  }

  public static class ExiftoolCommandResultSupport extends CommandResultSupport implements
      ExiftoolCommandResult {

    protected ExiftoolCommandResultSupport(final ProcessResult processResult) {
      super(processResult);
    }
  }

}
