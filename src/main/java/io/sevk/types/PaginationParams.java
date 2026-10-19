package io.sevk.types;

public class PaginationParams {
    public Integer page;
    public Integer limit;
    public String after;
    public String before;

    public PaginationParams page(Integer page) {
        this.page = page;
        return this;
    }

    public PaginationParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public PaginationParams after(String after) {
        this.after = after;
        return this;
    }

    public PaginationParams before(String before) {
        this.before = before;
        return this;
    }
}
