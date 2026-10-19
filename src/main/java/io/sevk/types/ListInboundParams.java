package io.sevk.types;

public class ListInboundParams extends ListParams {
    public InboundEmailStatus status;
    public String domainId;
    public String broadcastId;
    public Boolean isSpam;
    public Boolean isVirus;

    public ListInboundParams page(Integer page) {
        this.page = page;
        return this;
    }

    public ListInboundParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ListInboundParams after(String after) {
        this.after = after;
        return this;
    }

    public ListInboundParams before(String before) {
        this.before = before;
        return this;
    }

    public ListInboundParams search(String search) {
        this.search = search;
        return this;
    }

    public ListInboundParams status(InboundEmailStatus status) {
        this.status = status;
        return this;
    }

    public ListInboundParams domainId(String domainId) {
        this.domainId = domainId;
        return this;
    }

    public ListInboundParams broadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
        return this;
    }

    public ListInboundParams isSpam(Boolean isSpam) {
        this.isSpam = isSpam;
        return this;
    }

    public ListInboundParams isVirus(Boolean isVirus) {
        this.isVirus = isVirus;
        return this;
    }
}
