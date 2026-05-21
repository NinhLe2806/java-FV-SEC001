package org.example.core;

import org.example.model.AdRecord;
import org.example.model.AggregateStats;
import org.example.model.AggregationResult;

import java.util.TreeMap;

public final class Aggregator {
    private final TreeMap<String, AggregateStats> byCampaign = new TreeMap<>();

    public void accept(AdRecord record) {
        byCampaign.computeIfAbsent(record.campaignId(), ignored -> new AggregateStats()).add(record);
    }

    public AggregationResult snapshot() {
        return new AggregationResult(new TreeMap<>(byCampaign));
    }
}
