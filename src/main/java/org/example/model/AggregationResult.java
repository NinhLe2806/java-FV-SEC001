package org.example.model;

import java.util.Map;

public record AggregationResult(
        Map<String, AggregateStats> byCampaign
) {
}
