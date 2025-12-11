package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Topics resource for managing audience topics.
 */
public class Topics {
    private final SevkClient client;

    public Topics(SevkClient client) {
        this.client = client;
    }

    /**
     * List all topics for an audience.
     */
    public List<Topic> list(String audienceId) {
        return list(audienceId, null);
    }

    /**
     * List topics with pagination.
     */
    public List<Topic> list(String audienceId, ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        TopicListResponse response = client.get("/audiences/" + audienceId + "/topics", queryParams, TopicListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a topic by ID.
     */
    public Topic get(String audienceId, String topicId) {
        return client.get("/audiences/" + audienceId + "/topics/" + topicId, Topic.class);
    }

    /**
     * Create a new topic.
     */
    public Topic create(String audienceId, CreateTopicRequest request) {
        return client.post("/audiences/" + audienceId + "/topics", request, Topic.class);
    }

    /**
     * Update a topic.
     */
    public Topic update(String audienceId, String topicId, UpdateTopicRequest request) {
        return client.put("/audiences/" + audienceId + "/topics/" + topicId, request, Topic.class);
    }

    /**
     * Delete a topic.
     */
    public void delete(String audienceId, String topicId) {
        client.delete("/audiences/" + audienceId + "/topics/" + topicId);
    }
}
