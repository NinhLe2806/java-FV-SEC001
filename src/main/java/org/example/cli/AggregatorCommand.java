package org.example.cli;

import lombok.extern.slf4j.Slf4j;
import org.example.core.AdAggregatorService;
import org.example.io.CsvValidationException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@Slf4j
@Command(
        name = "ad-aggregator",
        mixinStandardHelpOptions = true,
        version = "ad-aggregator 1.0",
        description = "Processes a large advertising CSV file and writes campaign-level aggregates."
)
public final class AggregatorCommand implements Callable<Integer> {
    @Option(names = {"-i", "--input"}, required = true, description = "Path to input CSV file.")
    private Path input;

    @Option(names = {"-o", "--output"}, required = true, description = "Directory where result files are written.")
    private Path output;

    @Option(names = "--delimiter", defaultValue = ",", description = "CSV delimiter. Defaults to comma.")
    private char delimiter;

    @Override
    public Integer call() {
        try {
            new AdAggregatorService().process(input, output, delimiter);
            log.info("Aggregation completed. Output directory: {}", output.toAbsolutePath());
            return 0;
        } catch (CsvValidationException e) {
            log.error("Aggregation failed: {}", e.getMessage());
            return 1;
        } catch (Exception e) {
            log.error("Aggregation failed", e);
            return 1;
        }
    }
}
