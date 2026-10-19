package io.sevk.types;

public class ListDomainsParams {
    public Integer page;
    public Integer limit;
    public String after;
    public String before;
    public Boolean verified;

    public ListDomainsParams page(Integer page) {
        this.page = page;
        return this;
    }
    public ListDomainsParams limit(Integer limit) {
        this.limit = limit;
        return this;
    }
    public ListDomainsParams after(String after) {
        this.after = after;
        return this;
    }
    public ListDomainsParams before(String before) {
        this.before = before;
        return this;
    }
    public ListDomainsParams verified(Boolean verified) {
        this.verified = verified;
        return this;
    }
}
