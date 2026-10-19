package io.sevk.types;

import java.util.List;

public class BroadcastEmailListResponse {
    public List<BroadcastEmail> items;
    public Integer total;
    public Integer page;
    public Integer totalPages;
    public Boolean hasMore;
    public String nextCursor;
    public String prevCursor;
}
