package org.example.core;

import org.example.io.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdAggregatorServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void writesExpectedOutputsForSampleInput() throws IOException {
        Path input = tempDir.resolve("sample.csv");
        Files.writeString(input, String.join("\n",
                "campaign_id,date,impressions,clicks,spend,conversions",
                "CMP001,2025-01-01,12000,300,45.50,12",
                "CMP002,2025-01-01,8000,120,28.00,4",
                "CMP001,2025-01-02,14000,340,48.20,15",
                "CMP003,2025-01-01,5000,60,15.00,3",
                "CMP002,2025-01-02,8500,150,31.00,5",
                ""));

        Path output = tempDir.resolve("results");
        new AdAggregatorService().process(input, output, ',');

        assertThat(Files.readString(output.resolve("campaign_summary.csv"))).isEqualTo(String.join("\n",
                "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA",
                "CMP001,26000,640,93.70,27,0.0246,3.47",
                "CMP002,16500,270,59.00,9,0.0164,6.56",
                "CMP003,5000,60,15.00,3,0.0120,5.00",
                ""));

        assertThat(Files.readString(output.resolve("top10_ctr.csv"))).isEqualTo(String.join("\n",
                "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA",
                "CMP001,26000,640,93.70,27,0.0246,3.47",
                "CMP002,16500,270,59.00,9,0.0164,6.56",
                "CMP003,5000,60,15.00,3,0.0120,5.00",
                ""));

        assertThat(Files.readString(output.resolve("top10_cpa.csv"))).isEqualTo(String.join("\n",
                "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA",
                "CMP001,26000,640,93.70,27,0.0246,3.47",
                "CMP003,5000,60,15.00,3,0.0120,5.00",
                "CMP002,16500,270,59.00,9,0.0164,6.56",
                ""));
    }

    @Test
    void throwsForMissingInputFile() {
        Path missing = tempDir.resolve("missing.csv");
        Path output = tempDir.resolve("results");

        assertThatThrownBy(() -> new AdAggregatorService().process(missing, output, ','))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    void throwsForMalformedRow() throws IOException {
        Path input = tempDir.resolve("malformed.csv");
        Files.writeString(input, String.join("\n",
                "campaign_id,date,impressions,clicks,spend,conversions",
                "CMP001,2025-01-01,12000,300,45.50,12",
                "CMP002,2025-01-01,broken,120,28.00,4",
                ""));

        assertThatThrownBy(() -> new AdAggregatorService().process(input, tempDir.resolve("results"), ','))
                .isInstanceOf(CsvValidationException.class)
                .hasMessageContaining("line 3");
    }

    @Test
    void leavesCpaBlankWhenConversionsAreZero() throws IOException {
        Path input = tempDir.resolve("zero-conversions.csv");
        Files.writeString(input, String.join("\n",
                "campaign_id,date,impressions,clicks,spend,conversions",
                "CMP001,2025-01-01,100,10,20.00,0",
                ""));

        Path output = tempDir.resolve("results");
        new AdAggregatorService().process(input, output, ',');

        assertThat(Files.readString(output.resolve("campaign_summary.csv"))).isEqualTo(String.join("\n",
                "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA",
                "CMP001,100,10,20.00,0,0.1000,",
                ""));
    }
}
