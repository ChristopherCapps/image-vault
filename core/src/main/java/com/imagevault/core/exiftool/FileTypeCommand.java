package com.imagevault.core.exiftool;

import com.imagevault.core.exiftool.FileTypeCommand.FileTypeCommandResult;
import com.imagevault.io.exiftool.Exiftool.Tag;
import java.nio.file.Path;

public class FileTypeCommand extends ExiftoolCommand<FileTypeCommandResult> {

  public static FileTypeCommand of(final Path path) {
    return FileTypeCommand.newBuilder()
        .withPath(path)
        .withExtractTags(Tag.FileName, Tag.Directory)
        .withIoVeryShortOutputFormat()
        .withProcessingExtension("")
        .build();
  }

  private FileTypeCommand() {
    // builder only
  }

  @Override
  protected FileTypeCommandResult buildCommandResult(ProcessResult processResult) {
    return new DefaultFileTypeCommandResult(processResult);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends ExiftoolCommand.Builder<FileTypeCommand> {

    @Override
    protected FileTypeCommand newObject() {
      return new FileTypeCommand();
    }
  }

  public interface FileTypeCommandResult extends ExiftoolCommandResult {

  }

  static class DefaultFileTypeCommandResult extends ExiftoolCommandResultSupport implements
      FileTypeCommandResult {

    protected DefaultFileTypeCommandResult(ProcessResult processResult) {
      super(processResult);
    }
  }

}
