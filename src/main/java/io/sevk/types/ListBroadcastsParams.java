package io.sevk.types;

public class ListBroadcastsParams extends ListParams {
    public BroadcastLifecycleStatus status;

    public ListBroadcastsParams page(Integer page) {
        this.page = page;
        return this;
    }

    public ListBroadcastsParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ListBroadcastsParams after(String after) {
        this.after = after;
        return this;
    }

    public ListBroadcastsParams before(String before) {
        this.before = before;
        return this;
    }

    public ListBroadcastsParams search(String search) {
        this.search = search;
        return this;
    }

    public ListBroadcastsParams status(BroadcastLifecycleStatus status) {
        this.status = status;
        return this;
    }
}
