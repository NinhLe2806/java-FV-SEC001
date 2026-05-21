package org.example.io;

import org.example.model.AggregateStats;
import org.example.model.AggregationResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class AggregationWriter {
    private static final int TOP_LIMIT = 10;
    private static final String NEW_LINE = "\n";
    private static final Comparator<Map.Entry<String, AggregateStats>> CTR_ORDER =
            Comparator.<Map.Entry<String, AggregateStats>, BigDecimal>comparing(entry -> entry.getValue().ctr())
                    .reversed()
                    .thenComparing(Map.Entry::getKey);
    private static final Comparator<Map.Entry<String, AggregateStats>> CPA_ORDER =
            Comparator.<Map.Entry<String, AggregateStats>, BigDecimal>comparing(entry -> entry.getValue().cpa())
                    .thenComparing(Map.Entry::getKey);

    public void write(Path output, AggregationResult result) throws IOException {
        writeCampaignSummary(output.resolve("campaign_summary.csv"), result.byCampaign());
        writeTop10Ctr(output.resolve("top10_ctr.csv"), result.byCampaign());
        writeTop10Cpa(output.resolve("top10_cpa.csv"), result.byCampaign());
    }

    private static void writeCampaignSummary(Path path, Map<String, AggregateStats> campaigns) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(header());
            writer.write(NEW_LINE);
            campaigns.forEach((campaignId, stats) -> writeLine(writer, campaignId, stats));
        }
    }

    private static void writeTop10Ctr(Path path, Map<String, AggregateStats> campaigns) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(header());
            writer.write(NEW_LINE);
            selectTopCampaigns(campaigns, CTR_ORDER, entry -> true)
                    .forEach(entry -> writeLine(writer, entry.getKey(), entry.getValue()));
        }
    }

    private static void writeTop10Cpa(Path path, Map<String, AggregateStats> campaigns) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(header());
            writer.write(NEW_LINE);
            selectTopCampaigns(campaigns, CPA_ORDER, entry -> entry.getValue().hasConversions())
                    .forEach(entry -> writeLine(writer, entry.getKey(), entry.getValue()));
        }
    }

    private static String header() {
        return "campaign_id,total_impressions,total_clicks,total_spend,total_conversions,CTR,CPA";
    }

    private static void writeLine(BufferedWriter writer, String campaignId, AggregateStats stats) {
        try {
            writer.write(String.join(",",
                    campaignId,
                    Long.toString(stats.totalImpressions()),
                    Long.toString(stats.totalClicks()),
                    scaleMoney(stats.totalSpend()),
                    Long.toString(stats.totalConversions()),
                    scaleRatio(stats.ctr()),
                    scaleCpa(stats.cpa())
            ));
            writer.write(NEW_LINE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String scaleMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String scaleRatio(BigDecimal ratio) {
        return ratio.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    private static String scaleCpa(BigDecimal cpa) {
        return cpa == null ? "" : cpa.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static List<Map.Entry<String, AggregateStats>> selectTopCampaigns(
            Map<String, AggregateStats> campaigns,
            Comparator<Map.Entry<String, AggregateStats>> ranking,
            java.util.function.Predicate<Map.Entry<String, AggregateStats>> filter
    ) {
        Comparator<Map.Entry<String, AggregateStats>> heapOrder = ranking.reversed();
        PriorityQueue<Map.Entry<String, AggregateStats>> heap = new PriorityQueue<>(heapOrder);

        for (Map.Entry<String, AggregateStats> entry : campaigns.entrySet()) {
            if (!filter.test(entry)) {
                continue;
            }

            heap.offer(entry);
            if (heap.size() > TOP_LIMIT) {
                heap.poll();
            }
        }

        return heap.stream()
                .sorted(ranking)
                .toList();
    }
}
