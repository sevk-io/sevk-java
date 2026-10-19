package io.sevk.types;

public class UpdateInboundQuotaRequest {
    private Integer monthlyEmailLimit;
    private transient boolean monthlyEmailLimitPresent;

    public UpdateInboundQuotaRequest monthlyEmailLimit(int monthlyEmailLimit) {
        this.monthlyEmailLimit = monthlyEmailLimit;
        this.monthlyEmailLimitPresent = true;
        return this;
    }

    public UpdateInboundQuotaRequest clearMonthlyEmailLimit() {
        this.monthlyEmailLimit = null;
        this.monthlyEmailLimitPresent = true;
        return this;
    }

    public boolean hasMonthlyEmailLimit() {
        return monthlyEmailLimitPresent;
    }

    public Integer getMonthlyEmailLimit() {
        return monthlyEmailLimit;
    }
}
