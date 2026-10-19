package io.sevk.types;

public class AbTestConfigInput {
    public Integer testPercent;
    public Integer evalAfterMinutes;
    public AbTestMetric winnerMetric;

    public AbTestConfigInput testPercent(Integer testPercent) {
        this.testPercent = testPercent;
        return this;
    }

    public AbTestConfigInput evalAfterMinutes(Integer evalAfterMinutes) {
        this.evalAfterMinutes = evalAfterMinutes;
        return this;
    }

    public AbTestConfigInput winnerMetric(AbTestMetric winnerMetric) {
        this.winnerMetric = winnerMetric;
        return this;
    }
}
