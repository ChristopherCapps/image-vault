package com.imagevault.core;

import com.imagevault.core.exiftool.FileTypeCommand;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Engine {

  public static void main(String[] args) {
    setLevel(Level.ALL);
    System.out.println(
        FileTypeCommand.of(Path.of(args[0])).run());
  }

  public static void setLevel(Level targetLevel) {
    Logger root = Logger.getLogger("");
    root.setLevel(targetLevel);
    for (Handler handler : root.getHandlers()) {
      handler.setLevel(targetLevel);
    }
  }

  // init
  // import
  // import --simulated
  // export
}
