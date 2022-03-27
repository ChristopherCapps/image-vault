package com.imagevault.core.exiftool;

import com.imagevault.core.exiftool.VersionCommand.VersionCommandResult;
import com.imagevault.io.exiftool.Exiftool;

public class VersionCommand extends ExiftoolCommand<VersionCommandResult> {

  public static VersionCommand of() {
    return VersionCommand.newBuilder()
        .withOption(Exiftool.OPTION_VER)
        .build();
  }

  private VersionCommand() {
    // builder only
  }

  protected VersionCommandResult buildCommandResult(final ProcessResult processResult) {
    return new VersionCommandResult(processResult);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder extends ExiftoolCommand.Builder<VersionCommand> {

    @Override
    protected VersionCommand newObject() {
      return new VersionCommand();
    }
  }

  public static class VersionCommandResult extends ExiftoolCommandResultSupport {

    protected VersionCommandResult(final ProcessResult processResult) {
      super(processResult);
    }

    public String getVersion() {
      return getRequiredOutput().trim();
    }
  }

}
