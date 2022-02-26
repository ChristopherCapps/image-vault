package com.imagevault.io.exiftool;

import com.imagevault.io.System;
import java.nio.file.Path;

public class Exiftool {

  public final static String EXIFTOOL_EXECUTABLE_ENV = "EXIFTOOL_EXECUTABLE";

  public final static String EXECUTABLE_MAC = "exiftool";
  public final static String EXECUTABLE_LINUX = "exiftool";
  public final static String EXECUTABLE_WIN = "exiftool.exe";

  public final static String OPTION_TAG_PREFIX = "-";
  public final static String OPTION_JSON_OUTPUT = "-json";

  public static Path getExecutable() {
    final String defaultExecutable = switch (System.getPlatform()) {
      case LINUX -> EXECUTABLE_LINUX;
      case MACOS -> EXECUTABLE_MAC;
      case WINDOWS -> EXECUTABLE_WIN;
    };
    return System.Paths.getPathForFile(
        System.Environment.getVariable(EXIFTOOL_EXECUTABLE_ENV,
            defaultExecutable));
  }

  // create withArgument in Process
  // do withTag here
  // withCondition
  // withOutput
  // withTagsFromFile
  // withCopyTag(src, dest)
  // withFilenamePattern

  public enum Tag {
    FileName,
    Directory,
    FileSize,
    DateTimeOriginal
  }
}
