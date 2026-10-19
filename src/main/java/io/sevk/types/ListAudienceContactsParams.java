package io.sevk.types;

public class ListAudienceContactsParams extends ListParams {
    public Boolean subscribed;
    public String excludeTopicId;

    public ListAudienceContactsParams page(Integer page) {
        this.page = page;
        return this;
    }

    public ListAudienceContactsParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ListAudienceContactsParams after(String after) {
        this.after = after;
        return this;
    }

    public ListAudienceContactsParams before(String before) {
        this.before = before;
        return this;
    }

    public ListAudienceContactsParams search(String search) {
        this.search = search;
        return this;
    }

    public ListAudienceContactsParams subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }

    public ListAudienceContactsParams excludeTopicId(String excludeTopicId) {
        this.excludeTopicId = excludeTopicId;
        return this;
    }
}
