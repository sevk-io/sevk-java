package io.sevk.types;

public class UpdateOutboundQuotaRequest {
    public Integer monthlyEmailLimit;

    public UpdateOutboundQuotaRequest monthlyEmailLimit(Integer monthlyEmailLimit) {
        this.monthlyEmailLimit = monthlyEmailLimit;
        return this;
    }
}
