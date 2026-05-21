package org.example.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AggregateStats {
    private long totalImpressions;
    private long totalClicks;
    private BigDecimal totalSpend = BigDecimal.ZERO;
    private long totalConversions;

    public void add(AdRecord record) {
        totalImpressions += record.impressions();
        totalClicks += record.clicks();
        totalSpend = totalSpend.add(record.spend());
        totalConversions += record.conversions();
    }

    public long totalImpressions() {
        return totalImpressions;
    }

    public long totalClicks() {
        return totalClicks;
    }

    public BigDecimal totalSpend() {
        return totalSpend;
    }

    public long totalConversions() {
        return totalConversions;
    }

    public BigDecimal ctr() {
        return ratio(totalClicks, totalImpressions);
    }

    public BigDecimal cpa() {
        if (totalConversions == 0) {
            return null;
        }
        return totalSpend.divide(BigDecimal.valueOf(totalConversions), 6, RoundingMode.HALF_UP);
    }

    public boolean hasConversions() {
        return totalConversions > 0;
    }

    private static BigDecimal ratio(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 6, RoundingMode.HALF_UP);
    }
}
