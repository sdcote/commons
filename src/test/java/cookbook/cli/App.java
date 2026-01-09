package cookbook.cli;

import coyote.commons.cli.*;

public class App {
    public static void main(String[] args) throws ArgumentException {
        final Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("v", "version", false, "show version");
        options.addOption("c", "config", true, "config file");
        options.addOption("l", "log", true, "log file");
        options.addOption("d", "debug", false, "debug");

        ArgumentParser parser = new PosixParser();

        ArgumentList argList = parser.parse(options, args);
        if (argList.hasOption("v")) {
            System.out.println("v1.23");
            System.exit(0);
        } else if (argList.hasOption('h')) {
            System.out.println("Help!");
            System.exit(0);
        }

        if (argList.hasOption("c")) {
            String configFile = argList.getOptionValue('c');
            System.out.println("Configuration file name: " + configFile);
        }
    }
}
