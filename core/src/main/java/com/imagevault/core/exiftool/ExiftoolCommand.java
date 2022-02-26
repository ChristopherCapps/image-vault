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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExiftoolCommand<TResult extends ExiftoolCommandResult> extends
    CommandSupport<TResult> {

  private static final Logger logger = LoggerFactory.getLogger(ExiftoolCommand.class);

  @NotNull
  protected List<String> tags = new LinkedList<>();
  protected List<String> options = new LinkedList<>();
  protected List<Path> paths = new LinkedList<>();

  protected ExiftoolCommand() {
  }

  @Override
  protected TResult buildCommandResult(ProcessResult processResult) {
    return (TResult) new ExiftoolCommandResultSupport(processResult);
  }

  @Override
  protected List<String> buildArguments() {
    final List<String> arguments = new LinkedList<>();

    options.forEach(arguments::add);
    tags.stream().forEach(tag -> addTag(tag, arguments));
    paths.stream().map(Path::toAbsolutePath).map(Path::toString).forEach(arguments::add);

    return arguments;
  }

  private void addTag(final String tag, final List<String> arguments) {
    arguments.add(Exiftool.OPTION_TAG_PREFIX + tag);
  }

  public abstract static class Builder<TCommand extends ExiftoolCommand<TResult>,
      TResult extends ExiftoolCommandResult> extends CommandSupport.Builder<TCommand, TResult> {

    protected Builder() {
      withExecutable(Exiftool.getExecutable());
    }

    public Builder<TCommand, TResult> withOption(final String option) {
      set(o -> o.options.add(option));
      return this;
    }

    public Builder<TCommand, TResult> withJsonOption() {
      return set(o -> o.options.add(Exiftool.OPTION_JSON_OUTPUT));
      return this;
    }

    public Builder<TCommand, TResult> withTag(final Tag tag) {
      return withTag(tag.name());
    }

    public Builder<TCommand, TResult> withTags(final Tag... tag) {
      Arrays.stream(tag).sequential()
          .forEach(this::withTag);
      return this;
    }

    public Builder<TCommand, TResult> withTag(final String tag) {
      set(o -> o.tags.add(tag));
      return this;
    }

    public Builder<TCommand, TResult> withPath(final Path path) {
      set(o -> o.paths.add(path));
      return this;
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
