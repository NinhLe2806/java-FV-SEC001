package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdRecord(
        String campaignId,
        LocalDate date,
        long impressions,
        long clicks,
        BigDecimal spend,
        long conversions
) {
}
