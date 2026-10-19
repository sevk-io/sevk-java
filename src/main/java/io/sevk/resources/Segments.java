package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.CreateSegmentRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.ListParams;
import io.sevk.types.PreviewSegmentRequest;
import io.sevk.types.PreviewSegmentResponse;
import io.sevk.types.Segment;
import io.sevk.types.SegmentCalculateResponse;
import io.sevk.types.SegmentListResponse;
import io.sevk.types.UpdateSegmentRequest;
import java.util.HashMap;
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
     * Lists segments belonging to an audience using default pagination.
     */
    public SegmentListResponse list(String audienceId) {
        return list(audienceId, null);
    }

    /**
     * Lists segments belonging to an audience with optional filtering.
     */
    public SegmentListResponse list(String audienceId, ListParams params) {
        Map<String, String> queryParams = new HashMap<>();

        if (params != null) {
            if (params.page != null) {
                queryParams.put("page", params.page.toString());
            }
            if (params.limit != null) {
                queryParams.put("limit", params.limit.toString());
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

        return client.get("/audiences/" + audienceId + "/segments", queryParams, SegmentListResponse.class);
    }

    /**
     * Creates a saved contact segment under an audience.
     */
    public Segment create(String audienceId, CreateSegmentRequest request) {
        return client.post("/audiences/" + audienceId + "/segments", request, Segment.class);
    }

    /**
     * Evaluates segment rules and returns matches without saving a segment.
     */
    public PreviewSegmentResponse preview(String audienceId, PreviewSegmentRequest request) {
        return client.post("/audiences/" + audienceId + "/segments/preview", request, PreviewSegmentResponse.class);
    }

    /**
     * Retrieves a saved segment by ID.
     */
    public Segment get(String segmentId) {
        return client.get("/segments/" + segmentId, Segment.class);
    }

    /**
     * Updates a segment while preserving omitted fields.
     */
    public Segment update(String segmentId, UpdateSegmentRequest request) {
        return client.put("/segments/" + segmentId, request, Segment.class);
    }

    /**
     * Deletes a segment without deleting any contacts.
     */
    public DeleteResponse delete(String segmentId) {
        return client.delete("/segments/" + segmentId, DeleteResponse.class);
    }

    /**
     * Recalculates and returns the current matching contact count.
     */
    public SegmentCalculateResponse calculate(String segmentId) {
        return client.get("/segments/" + segmentId + "/calculate", SegmentCalculateResponse.class);
    }
}
