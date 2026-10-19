package io.sevk.types;

public class ListTopicContactsParams extends ListParams {
    public Boolean subscribed;

    public ListTopicContactsParams page(Integer page) {
        this.page = page;
        return this;
    }

    public ListTopicContactsParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ListTopicContactsParams after(String after) {
        this.after = after;
        return this;
    }

    public ListTopicContactsParams before(String before) {
        this.before = before;
        return this;
    }

    public ListTopicContactsParams search(String search) {
        this.search = search;
        return this;
    }

    public ListTopicContactsParams subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }
}
