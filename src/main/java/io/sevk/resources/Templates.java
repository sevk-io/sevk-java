package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.CreateTemplateRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.ListParams;
import io.sevk.types.Template;
import io.sevk.types.TemplateListResponse;
import io.sevk.types.UpdateTemplateRequest;
import java.util.HashMap;
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
     * Lists reusable email templates using default pagination.
     */
    public TemplateListResponse list() {
        return list(null);
    }

    /**
     * Lists reusable email templates with optional pagination and search.
     */
    public TemplateListResponse list(ListParams params) {
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

        return client.get("/templates", queryParams, TemplateListResponse.class);
    }

    /**
     * Retrieves a template by ID.
     */
    public Template get(String id) {
        return client.get("/templates/" + id, Template.class);
    }

    /**
     * Creates a reusable email template.
     */
    public Template create(CreateTemplateRequest request) {
        return client.post("/templates", request, Template.class);
    }

    /**
     * Updates a template while preserving omitted fields.
     */
    public Template update(String id, UpdateTemplateRequest request) {
        return client.put("/templates/" + id, request, Template.class);
    }

    /**
     * Deletes a template without affecting email already sent from it.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/templates/" + id, DeleteResponse.class);
    }

    /**
     * Creates an independent copy of a template for further editing.
     */
    public Template duplicate(String id) {
        return client.post("/templates/" + id + "/duplicate", null, Template.class);
    }
}
