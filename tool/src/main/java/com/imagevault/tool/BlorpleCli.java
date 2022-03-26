package com.imagevault.tool;

import com.imagevault.core.Engine;
import com.imagevault.io.Console;
import com.imagevault.io.Persistence;
import java.io.PrintWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.output.StringBuilderWriter;

public class BlorpleCli {

  public static void main(String[] args) {

    Console.initialize();
    Persistence.initialize();

    final Engine engine = Engine.of();

    CommandLineParser cliParser = new DefaultParser();
    try {
      final CommandLine cli = cliParser.parse(Cli.OPTIONS, args);
      if (cli.getOptions().length == 0) {
        Console.abend("no options specified\n\n%s", buildHelp(""));
      } else if (cli.hasOption(Cli.INIT_OPTION)) {
        engine.init();
      }
    } catch (ParseException pe) {
      Console.abend("%s\n\n%s", pe.getMessage(), buildHelp(""));
    }
  }

  private static String buildHelp(final String footer) {
    StringBuilderWriter writer = new StringBuilderWriter();
    new HelpFormatter().printHelp(
        new PrintWriter(writer),
        50,
        "blorple <command> [options]",
        "Create and manage repositories of media files\n",
        Cli.OPTIONS,
        2,
        4,
        footer);
    return writer.toString();
  }

  static class Cli {

    private static final Option INIT_OPTION =
        Option.builder()
            .option("init")
            .longOpt("initialize")
            .desc("Establishes the current directory as the root of a new repository")
            .build();

    private static final Options OPTIONS =
        new Options()
            .addOption(INIT_OPTION);
  }

  // init
  // import
  // import --simulated
  // export
}
