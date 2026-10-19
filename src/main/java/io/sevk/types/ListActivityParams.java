package io.sevk.types;

import java.util.Arrays;
import java.util.List;

public class ListActivityParams {
    public Integer page;
    public Integer limit;
    public String after;
    public String before;
    public ActivityCategory category;
    public List<ActivityAction> type;
    public Integer days;
    public String search;

    public ListActivityParams page(Integer page) {
        this.page = page;
        return this;
    }

    public ListActivityParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public ListActivityParams after(String after) {
        this.after = after;
        return this;
    }

    public ListActivityParams before(String before) {
        this.before = before;
        return this;
    }

    public ListActivityParams category(ActivityCategory category) {
        this.category = category;
        return this;
    }

    public ListActivityParams type(ActivityAction... type) {
        this.type = Arrays.asList(type);
        return this;
    }

    public ListActivityParams days(Integer days) {
        this.days = days;
        return this;
    }

    public ListActivityParams search(String search) {
        this.search = search;
        return this;
    }
}
