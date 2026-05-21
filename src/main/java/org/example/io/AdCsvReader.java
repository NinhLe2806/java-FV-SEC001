package org.example.io;

import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.example.model.AdRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class AdCsvReader {
    private static final List<String> EXPECTED_HEADERS = List.of(
            "campaign_id",
            "date",
            "impressions",
            "clicks",
            "spend",
            "conversions"
    );

    private final char delimiter;

    public AdCsvReader(char delimiter) {
        this.delimiter = delimiter;
    }

    public void read(Path input, Consumer<AdRecord> consumer) throws IOException {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.setIgnoreLeadingWhitespaces(true);
        settings.setIgnoreTrailingWhitespaces(true);
        settings.setSkipEmptyLines(true);
        settings.getFormat().setDelimiter(delimiter);
        settings.setLineSeparatorDetectionEnabled(true);
        settings.setProcessor(new AbstractRowProcessor() {
            @Override
            public void processStarted(com.univocity.parsers.common.ParsingContext context) {
                String[] headers = context.headers();
                validateHeaders(headers);
            }

            @Override
            public void rowProcessed(String[] row, com.univocity.parsers.common.ParsingContext context) {
                consumer.accept(parse(row, context.currentLine()));
            }
        });

        try (var reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            CsvParser parser = new CsvParser(settings);
            parser.parse(reader);
        } catch (CsvValidationException e) {
            throw e;
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CsvValidationException validationException) {
                throw validationException;
            }
            throw e;
        }
    }

    private static void validateHeaders(String[] headers) {
        if (headers == null || !EXPECTED_HEADERS.equals(Arrays.asList(headers))) {
            throw new CsvValidationException("Invalid CSV header. Expected: " + String.join(",", EXPECTED_HEADERS));
        }
    }

    private static AdRecord parse(String[] row, long lineNumber) {
        if (row.length < 6) {
            throw new CsvValidationException("Invalid CSV row at line " + lineNumber + ": expected 6 columns");
        }

        try {
            return new AdRecord(
                    requireValue(row[0], "campaign_id", lineNumber),
                    LocalDate.parse(requireValue(row[1], "date", lineNumber)),
                    Long.parseLong(requireValue(row[2], "impressions", lineNumber)),
                    Long.parseLong(requireValue(row[3], "clicks", lineNumber)),
                    new BigDecimal(requireValue(row[4], "spend", lineNumber)),
                    Long.parseLong(requireValue(row[5], "conversions", lineNumber))
            );
        } catch (RuntimeException e) {
            throw new CsvValidationException("Invalid CSV row at line " + lineNumber, e);
        }
    }

    private static String requireValue(String value, String fieldName, long lineNumber) {
        if (value == null) {
            throw new CsvValidationException("Invalid CSV row at line " + lineNumber + ": " + fieldName + " is blank");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new CsvValidationException("Invalid CSV row at line " + lineNumber + ": " + fieldName + " is blank");
        }
        return trimmed;
    }
}
