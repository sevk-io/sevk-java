package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Audiences resource for managing audiences.
 */
public class Audiences {
    private final SevkClient client;

    public Audiences(SevkClient client) {
        this.client = client;
    }

    /**
     * List all audiences.
     */
    public List<Audience> list() {
        return list(null);
    }

    /**
     * List audiences with pagination.
     */
    public List<Audience> list(ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        AudienceListResponse response = client.get("/audiences", queryParams, AudienceListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get an audience by ID.
     */
    public Audience get(String id) {
        return client.get("/audiences/" + id, Audience.class);
    }

    /**
     * Create a new audience.
     */
    public Audience create(CreateAudienceRequest request) {
        return client.post("/audiences", request, Audience.class);
    }

    /**
     * Update an audience.
     */
    public Audience update(String id, UpdateAudienceRequest request) {
        return client.put("/audiences/" + id, request, Audience.class);
    }

    /**
     * Delete an audience.
     */
    public void delete(String id) {
        client.delete("/audiences/" + id);
    }

    /**
     * Add contacts to an audience.
     */
    public void addContacts(String audienceId, List<String> contactIds) {
        Map<String, Object> body = new HashMap<>();
        body.put("contactIds", contactIds);
        client.post("/audiences/" + audienceId + "/contacts", body, Void.class);
    }

    /**
     * List contacts in an audience.
     */
    public List<io.sevk.types.Contact> listContacts(String audienceId) {
        return listContacts(audienceId, null);
    }

    /**
     * List contacts in an audience with pagination.
     */
    public List<io.sevk.types.Contact> listContacts(String audienceId, ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
        }
        AudienceContactListResponse response = client.get("/audiences/" + audienceId + "/contacts", queryParams, AudienceContactListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Remove a contact from an audience.
     */
    public void removeContact(String audienceId, String contactId) {
        client.delete("/audiences/" + audienceId + "/contacts/" + contactId);
    }
}
