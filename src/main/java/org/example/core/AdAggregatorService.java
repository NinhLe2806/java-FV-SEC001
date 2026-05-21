package org.example.core;

import lombok.extern.slf4j.Slf4j;
import org.example.io.AdCsvReader;
import org.example.io.AggregationWriter;
import org.example.model.AdRecord;
import org.example.model.AggregationResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public final class AdAggregatorService {
    public void process(Path input, Path output, char delimiter) throws IOException {
        long totalStartNanos = System.nanoTime();
        validateInput(input);
        Files.createDirectories(output);
        log.info("Starting aggregation. input={}, output={}", input.toAbsolutePath(), output.toAbsolutePath());

        Aggregator aggregator = new Aggregator();
        AdCsvReader reader = new AdCsvReader(delimiter);
        long readStartNanos = System.nanoTime();
        reader.read(input, (AdRecord record) -> aggregator.accept(record));
        long readDurationNanos = System.nanoTime() - readStartNanos;
        log.info("Finished reading and aggregating CSV in {} ms", toMillis(readDurationNanos));

        AggregationResult result = aggregator.snapshot();
        long writeStartNanos = System.nanoTime();
        new AggregationWriter().write(output, result);
        long writeDurationNanos = System.nanoTime() - writeStartNanos;
        long totalDurationNanos = System.nanoTime() - totalStartNanos;

        log.info("Finished writing output files in {} ms", toMillis(writeDurationNanos));
        log.info("Processing completed in {} ms", toMillis(totalDurationNanos));
    }

    private static void validateInput(Path input) throws IOException {
        if (!Files.isRegularFile(input)) {
            throw new IOException("Input file does not exist or is not a regular file: " + input);
        }
        if (!Files.isReadable(input)) {
            throw new IOException("Input file is not readable: " + input);
        }
    }

    private static long toMillis(long durationNanos) {
        return durationNanos / 1_000_000;
    }
}
