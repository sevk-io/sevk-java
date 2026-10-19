package io.sevk.types;

public class ListParams extends PaginationParams {
    public String search;

    public ListParams page(Integer page) {
        super.page(page);
        return this;
    }

    public ListParams limit(Integer limit) {
        super.limit(limit);
        return this;
    }

    public ListParams after(String after) {
        super.after(after);
        return this;
    }

    public ListParams before(String before) {
        super.before(before);
        return this;
    }

    public ListParams search(String search) {
        this.search = search;
        return this;
    }
}
