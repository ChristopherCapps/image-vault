package com.imagevault.io.exiftool;

import com.imagevault.io.Persistence.Logging;
import com.imagevault.io.Shell;
import com.imagevault.io.Shell.Platform;
import java.nio.file.Path;
import java.util.Locale;

public class Exiftool {

  public final static String EXECUTABLE_ENV = "EXIFTOOL_EXECUTABLE";

  public final static String EXECUTABLE_MAC = "exiftool";
  public final static String EXECUTABLE_LINUX = "exiftool";
  public final static String EXECUTABLE_WIN = "exiftool.exe";

  public final static String OPTION_VER = "-ver";

  public final static String OPTION_TAG_EXTRACT = "-";

  public final static String OPTION_IO_JSON_OUTPUT_FORMAT = "-json";
  public static final String OPTION_IO_VERY_SHORT_OUTPUT_FORMAT = "-S";

  public static final String OPTION_PROCESSING_EXTENSION = "-ext";

  public static Path getExecutable() {
    final Platform platform = Shell.getPlatform();
    Logging.debug("platform: %s", platform.name().toLowerCase(Locale.ROOT));
    final String defaultExecutable = switch (platform) {
      case LINUX -> EXECUTABLE_LINUX;
      case MACOS -> EXECUTABLE_MAC;
      case WINDOWS -> EXECUTABLE_WIN;
    };
    final Path selectedPath = Shell.Paths.getPathForFile(
        Shell.Environment.getVariable(EXECUTABLE_ENV,
            defaultExecutable));
    Logging.debug("exiftool: %s", selectedPath);
    return selectedPath;
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
