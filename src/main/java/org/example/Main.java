package org.example;

import org.example.cli.AggregatorCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new AggregatorCommand()).execute(args);
        System.exit(exitCode);
    }
}
