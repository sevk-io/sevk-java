package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Templates resource for managing email templates.
 */
public class Templates {
    private final SevkClient client;

    public Templates(SevkClient client) {
        this.client = client;
    }

    /**
     * List all templates.
     */
    public List<Template> list() {
        return list(null);
    }

    /**
     * List templates with pagination.
     */
    public List<Template> list(ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        TemplateListResponse response = client.get("/templates", queryParams, TemplateListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a template by ID.
     */
    public Template get(String id) {
        return client.get("/templates/" + id, Template.class);
    }

    /**
     * Create a new template.
     */
    public Template create(CreateTemplateRequest request) {
        return client.post("/templates", request, Template.class);
    }

    /**
     * Update a template.
     */
    public Template update(String id, UpdateTemplateRequest request) {
        return client.put("/templates/" + id, request, Template.class);
    }

    /**
     * Delete a template.
     */
    public void delete(String id) {
        client.delete("/templates/" + id);
    }

    /**
     * Duplicate a template.
     */
    public Template duplicate(String id) {
        return client.post("/templates/" + id + "/duplicate", null, Template.class);
    }
}
