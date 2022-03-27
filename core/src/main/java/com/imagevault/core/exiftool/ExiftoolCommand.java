package com.imagevault.core.exiftool;

import com.imagevault.core.Command.CommandResult.CommandResultSupport;
import com.imagevault.core.Command.CommandSupport;
import com.imagevault.core.exiftool.ExiftoolCommand.ExiftoolCommandResult;
import com.imagevault.io.exiftool.Exiftool;
import com.imagevault.io.exiftool.Exiftool.Tag;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class ExiftoolCommand<TResult extends ExiftoolCommandResult> extends
    CommandSupport<TResult> {

  protected ExiftoolCommand() {
  }

  @Override
  protected abstract TResult buildCommandResult(final ProcessResult processResult);

  private static String formatTagArgument(final String tag) {
    return Exiftool.OPTION_TAG_EXTRACT + tag;
  }

  public abstract static class Builder<TCommand extends ExiftoolCommand<?>>
      extends CommandSupport.Builder<TCommand, Builder<TCommand>> {

    protected List<String> tags = new LinkedList<>();
    protected List<String> options = new LinkedList<>();
    protected List<Path> paths = new LinkedList<>();

    @Override
    protected void buildArguments() {
      super.buildArguments();
      options.stream().forEach(this::withArgument);
      tags.stream().map(ExiftoolCommand::formatTagArgument).forEach(this::withArgument);
      paths.stream().map(Path::toAbsolutePath).map(Path::toString).forEach(this::withArgument);
    }

    protected Builder() {
      withExecutable(Exiftool.getExecutable());
    }

    public Builder<TCommand> withOption(final String option) {
      options.add(option);
      return this;
    }

    public Builder<TCommand> withIoJsonOutputFormat() {
      return withOption(Exiftool.OPTION_IO_JSON_OUTPUT_FORMAT);
    }

    public Builder<TCommand> withIoVeryShortOutputFormat() {
      return withOption(Exiftool.OPTION_IO_VERY_SHORT_OUTPUT_FORMAT);
    }

    public Builder<TCommand> withProcessingExtension(final String extension) {
      return withOption(formatOption(Exiftool.OPTION_PROCESSING_EXTENSION, extension));
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
      tags.add(tag);
      return this;
    }

    public Builder<TCommand> withPath(final Path path) {
      paths.add(path);
      return this;
    }

    private String formatOption(final String option, final String value) {
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
