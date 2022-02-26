package com.imagevault.core.exiftool;

import com.imagevault.core.exiftool.FileTypeCommand.FileTypeCommandResult;
import com.imagevault.io.exiftool.Exiftool.Tag;
import java.nio.file.Path;

public class FileTypeCommand extends ExiftoolCommand<FileTypeCommandResult> {

  public static FileTypeCommand of(final Path path) {
    return FileTypeCommand.newBuilder()
        .withPath(path)
        .withTags(Tag.FileName, Tag.Directory)
        .withOption("-S")
        .withOption("-json")
        .build();
  }

  private FileTypeCommand() {
    // builder only
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends
      ExiftoolCommand.Builder<FileTypeCommand, FileTypeCommandResult> {

    @Override
    protected FileTypeCommand newObject() {
      return new FileTypeCommand();
    }
  }

  interface FileTypeCommandResult extends ExiftoolCommandResult {

  }
}
