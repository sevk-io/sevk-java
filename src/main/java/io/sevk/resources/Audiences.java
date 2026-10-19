package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.AddContactsToAudienceResponse;
import io.sevk.types.Audience;
import io.sevk.types.AudienceContactListResponse;
import io.sevk.types.AudienceListResponse;
import io.sevk.types.AudienceMutationResponse;
import io.sevk.types.Contact;
import io.sevk.types.CreateAudienceRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.ListAudienceContactsParams;
import io.sevk.types.ListParams;
import io.sevk.types.RemoveContactResponse;
import io.sevk.types.UpdateAudienceRequest;
import java.util.Collections;
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
     * Lists audiences using default pagination.
     */
    public AudienceListResponse list() {
        return list(null);
    }

    /**
     * Lists audiences with optional pagination and search.
     */
    public AudienceListResponse list(ListParams params) {
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

        return client.get("/audiences", queryParams, AudienceListResponse.class);
    }

    /**
     * Retrieves an audience by ID.
     */
    public Audience get(String id) {
        return client.get("/audiences/" + id, Audience.class);
    }

    /**
     * Creates an audience for organizing contacts, topics, and segments.
     */
    public AudienceMutationResponse create(CreateAudienceRequest request) {
        return client.post("/audiences", request, AudienceMutationResponse.class);
    }

    /**
     * Updates an audience while preserving omitted fields.
     */
    public AudienceMutationResponse update(String id, UpdateAudienceRequest request) {
        return client.put("/audiences/" + id, request, AudienceMutationResponse.class);
    }

    /**
     * Deletes an audience without deleting its contacts.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/audiences/" + id, DeleteResponse.class);
    }

    /**
     * Adds contacts to an audience, ignoring existing memberships.
     */
    public AddContactsToAudienceResponse addContacts(String audienceId, List<String> contactIds) {
        Map<String, Object> body = new HashMap<>();

        body.put("contactIds", contactIds);

        return client.post("/audiences/" + audienceId + "/contacts", body, AddContactsToAudienceResponse.class);
    }

    /**
     * Lists contacts attached to an audience using default pagination.
     */
    public AudienceContactListResponse listContacts(String audienceId) {
        return listContacts(audienceId, null);
    }

    /**
     * Lists contacts attached to an audience with optional filtering.
     */
    public AudienceContactListResponse listContacts(String audienceId, ListParams params) {
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

            if (params instanceof ListAudienceContactsParams) {
                ListAudienceContactsParams ap = (ListAudienceContactsParams) params;

                if (ap.subscribed != null) {
                    queryParams.put("subscribed", ap.subscribed.toString());
                }
                if (ap.excludeTopicId != null) {
                    queryParams.put("excludeTopicId", ap.excludeTopicId);
                }
            }
        }

        return client.get("/audiences/" + audienceId + "/contacts", queryParams, AudienceContactListResponse.class);
    }

    /** Returns only contact items from the selected audience page. */
    public List<Contact> listContactItems(String audienceId, ListParams params) {
        AudienceContactListResponse response = listContacts(audienceId, params);

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }

    /**
     * Removes a contact from an audience without deleting it.
     */
    public RemoveContactResponse removeContact(String audienceId, String contactId) {
        return client.delete("/audiences/" + audienceId + "/contacts/" + contactId, RemoveContactResponse.class);
    }
}
