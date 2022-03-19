package com.imagevault.tool;

import com.imagevault.core.Engine;
import com.imagevault.io.Console;
import com.imagevault.io.Persistence;
import com.imagevault.io.Persistence.Logging;
import com.imagevault.io.Shell;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BlorpleCli {

  public static void main(String[] args) {

    Console.initialize();
    Persistence.initialize();
    Engine.initialize();

    CommandLineParser cliParser = new DefaultParser();
    try {
      final CommandLine cli = cliParser.parse(Cli.OPTIONS, args);
      //Console.print("Working Dir: %1$s", Shell.Paths.getWorkingDirectoryPath());
      Logging.debug("Working Dir: %s", Shell.Paths.getWorkingDirectoryPath());
      //Output.debug("Args: %1$s", args);
    } catch (ParseException pe) {
      System.err.println(pe.getMessage());
    }
  }

  static class Cli {

    private static final Option SAMPLE_OPTION =
        Option.builder()
            .option("sample")
            .desc("Enables sample mode")
            .build();

    private static final Options OPTIONS =
        new Options()
            .addOption(SAMPLE_OPTION);
  }

  // init
  // import
  // import --simulated
  // export
}
