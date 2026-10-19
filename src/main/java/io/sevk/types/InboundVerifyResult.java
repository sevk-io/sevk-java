package io.sevk.types;

import java.util.List;

public class InboundVerifyResult extends DomainRead {
    public List<InboundMxRecord> inboundMxResolved;
    public String inboundExpectedTarget;
}
