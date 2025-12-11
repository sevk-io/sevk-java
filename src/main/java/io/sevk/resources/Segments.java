package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Segments resource for managing audience segments.
 */
public class Segments {
    private final SevkClient client;

    public Segments(SevkClient client) {
        this.client = client;
    }

    /**
     * List all segments for an audience.
     */
    public List<Segment> list(String audienceId) {
        return list(audienceId, null);
    }

    /**
     * List segments with pagination.
     */
    public List<Segment> list(String audienceId, ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        SegmentListResponse response = client.get("/audiences/" + audienceId + "/segments", queryParams, SegmentListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a segment by ID.
     */
    public Segment get(String audienceId, String segmentId) {
        return client.get("/audiences/" + audienceId + "/segments/" + segmentId, Segment.class);
    }

    /**
     * Create a new segment.
     */
    public Segment create(String audienceId, CreateSegmentRequest request) {
        return client.post("/audiences/" + audienceId + "/segments", request, Segment.class);
    }

    /**
     * Update a segment.
     */
    public Segment update(String audienceId, String segmentId, UpdateSegmentRequest request) {
        return client.put("/audiences/" + audienceId + "/segments/" + segmentId, request, Segment.class);
    }

    /**
     * Delete a segment.
     */
    public void delete(String audienceId, String segmentId) {
        client.delete("/audiences/" + audienceId + "/segments/" + segmentId);
    }
}
