package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.ActivityListResponse;
import io.sevk.types.ActivityStats;
import io.sevk.types.ListActivityParams;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Activity resource for retrieving the activity audit log.
 */
public class Activity {
    private final SevkClient client;

    public Activity(SevkClient client) {
        this.client = client;
    }

    /**
     * Lists recent project activity.
     */
    public ActivityListResponse list() {
        return list((ListActivityParams) null);
    }

    /**
     * Lists project activity with optional pagination and filtering.
     */
    public ActivityListResponse list(ListActivityParams params) {
        Map<String, String> queryParams = new HashMap<>();

        if (params != null) {
            if (params.page != null) {
                queryParams.put("page", params.page.toString());
            }
            if (params.limit != null) {
                queryParams.put("limit", params.limit.toString());
            }
            if (params.category != null) {
                queryParams.put("category", params.category.wireValue());
            }
            if (params.type != null) {

                queryParams.put("type", params.type.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(",")));
            }
            if (params.days != null) {
                queryParams.put("days", params.days.toString());
            }
            if (params.search != null) {
                queryParams.put("search", params.search);
            }
            if (params.after != null) {
                queryParams.put("after", params.after);
            }
            if (params.before != null) {
                queryParams.put("before", params.before);
            }
        }

        return client.get("/activity", queryParams, ActivityListResponse.class);
    }

    /**
     * Returns aggregate counts and rates for recent project activity.
     */
    public ActivityStats stats() {
        return client.get("/activity/stats", ActivityStats.class);
    }
}
