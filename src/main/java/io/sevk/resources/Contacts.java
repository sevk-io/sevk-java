package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Contact;
import io.sevk.types.Types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contacts resource for managing contacts.
 */
public class Contacts {
    private final SevkClient client;

    public Contacts(SevkClient client) {
        this.client = client;
    }

    /**
     * List all contacts.
     */
    public List<Contact> list() {
        return list(null);
    }

    /**
     * List contacts with pagination.
     */
    public List<Contact> list(ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
            if (params.search != null) queryParams.put("search", params.search);
        }
        ContactListResponse response = client.get("/contacts", queryParams, ContactListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a contact by ID.
     */
    public Contact get(String id) {
        return client.get("/contacts/" + id, Contact.class);
    }

    /**
     * Create a new contact.
     */
    public Contact create(String email) {
        return create(email, null);
    }

    /**
     * Create a new contact with options.
     */
    public Contact create(String email, CreateContactRequest request) {
        if (request == null) {
            request = new CreateContactRequest();
        }
        request.email = email;
        return client.post("/contacts", request, Contact.class);
    }

    /**
     * Update a contact.
     */
    public Contact update(String id, UpdateContactRequest request) {
        return client.put("/contacts/" + id, request, Contact.class);
    }

    /**
     * Delete a contact.
     */
    public void delete(String id) {
        client.delete("/contacts/" + id);
    }

    /**
     * Bulk update contacts.
     */
    public BulkUpdateResponse bulkUpdate(List<BulkUpdateContactEntry> updates) {
        BulkUpdateContactRequest request = new BulkUpdateContactRequest().contacts(updates);
        return client.put("/contacts/bulk-update", request, BulkUpdateResponse.class);
    }

    /**
     * Import contacts.
     */
    public ImportContactsResponse importContacts(ImportContactsRequest request) {
        return client.post("/contacts/import", request, ImportContactsResponse.class);
    }

    /**
     * Get events for a contact.
     */
    public List<ContactEvent> getEvents(String id) {
        return getEvents(id, null);
    }

    /**
     * Get events for a contact with pagination.
     */
    public List<ContactEvent> getEvents(String id, ListParams params) {
        Map<String, String> queryParams = new HashMap<>();
        if (params != null) {
            if (params.page != null) queryParams.put("page", params.page.toString());
            if (params.limit != null) queryParams.put("limit", params.limit.toString());
        }
        ContactEventListResponse response = client.get("/contacts/" + id + "/events", queryParams, ContactEventListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }
}
