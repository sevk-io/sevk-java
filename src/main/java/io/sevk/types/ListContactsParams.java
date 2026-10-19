package io.sevk.types;

public class ListContactsParams extends ListParams {
    public String excludeAudience;
    public Boolean subscribed;

    public ListContactsParams page(Integer page) {
        this.page = page;
        return this;
    }

    public ListContactsParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ListContactsParams after(String after) {
        this.after = after;
        return this;
    }

    public ListContactsParams before(String before) {
        this.before = before;
        return this;
    }

    public ListContactsParams search(String search) {
        this.search = search;
        return this;
    }

    public ListContactsParams excludeAudience(String excludeAudience) {
        this.excludeAudience = excludeAudience;
        return this;
    }

    public ListContactsParams subscribed(Boolean subscribed) {
        this.subscribed = subscribed;
        return this;
    }
}
