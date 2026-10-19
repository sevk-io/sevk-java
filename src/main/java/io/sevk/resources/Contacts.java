package io.sevk.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.sevk.SevkClient;
import io.sevk.types.BulkUpdateContactEntry;
import io.sevk.types.BulkUpdateContactRequest;
import io.sevk.types.BulkUpdateResponse;
import io.sevk.types.Contact;
import io.sevk.types.ContactActivityListResponse;
import io.sevk.types.ContactListResponse;
import io.sevk.types.CreateContactRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.ImportContactsRequest;
import io.sevk.types.ImportContactsResponse;
import io.sevk.types.ListContactsParams;
import io.sevk.types.ListParams;
import io.sevk.types.PaginationParams;
import io.sevk.types.UpdateContactRequest;
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
     * Lists contacts using default pagination.
     */
    public ContactListResponse list() {
        return list(null);
    }

    /**
     * Lists contacts with optional pagination, search, and filters.
     */
    public ContactListResponse list(ListParams params) {
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

            if (params instanceof ListContactsParams) {
                ListContactsParams cp = (ListContactsParams) params;

                if (cp.excludeAudience != null) {
                    queryParams.put("excludeAudience", cp.excludeAudience);
                }
                if (cp.subscribed != null) {
                    queryParams.put("subscribed", cp.subscribed.toString());
                }
            }
        }

        return client.get("/contacts", queryParams, ContactListResponse.class);
    }

    /**
     * Retrieves a contact by ID.
     */
    public Contact get(String id) {
        return client.get("/contacts/" + id, Contact.class);
    }

    /**
     * Creates a subscribed contact with no custom metadata.
     */
    public Contact create(String email) {
        return create(email, null);
    }

    /**
     * Creates a contact and optionally attaches custom metadata.
     */
    public Contact create(String email, CreateContactRequest request) {
        CreateContactRequest wire = new CreateContactRequest().email(email);

        if (request != null) {
            wire.subscribed = request.subscribed;
            wire.data = request.data;
        }

        return client.post("/contacts", wire, Contact.class);
    }

    /**
     * Updates a contact while preserving omitted fields.
     */
    public Contact update(String id, UpdateContactRequest request) {
        if (request == null) {
            return client.put("/contacts/" + id, null, Contact.class);
        }

        JsonObject body = client.serializeObject(request);

        if (request.isDataSet() && request.data == null) {
            body.add("data", JsonNull.INSTANCE);
        }

        return client.put("/contacts/" + id, body, Contact.class);
    }

    /**
     * Deletes a contact and removes its audience and topic memberships.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/contacts/" + id, DeleteResponse.class);
    }

    /**
     * Updates contacts by email and reports entries that were not found.
     */
    public BulkUpdateResponse bulkUpdate(List<BulkUpdateContactEntry> updates) {
        BulkUpdateContactRequest request = new BulkUpdateContactRequest().contacts(updates);
        JsonObject body = client.serializeObject(request);

        if (updates != null && body.has("contacts") && body.get("contacts").isJsonArray()) {
            JsonArray contacts = body.getAsJsonArray("contacts");

            for (int i = 0; i < updates.size() && i < contacts.size(); i++) {
                BulkUpdateContactEntry entry = updates.get(i);

                if (entry != null && entry.isDataSet() && entry.data == null && contacts.get(i).isJsonObject()) {
                    contacts.get(i).getAsJsonObject().add("data", JsonNull.INSTANCE);
                }
            }
        }

        return client.put("/contacts/bulk-update", body, BulkUpdateResponse.class);
    }

    /**
     * Imports contacts and optionally attaches them to a group.
     */
    public ImportContactsResponse bulkImport(ImportContactsRequest request) {
        return client.post("/contacts/import", request, ImportContactsResponse.class);
    }

    /**
     * Lists delivery and engagement activity for a contact.
     */
    public ContactActivityListResponse activity(String id) {
        return activity(id, null);
    }

    /**
     * Lists delivery and engagement activity with pagination.
     */
    public ContactActivityListResponse activity(String id, PaginationParams params) {
        Map<String, String> queryParams = new HashMap<>();

        if (params != null) {
            if (params.page != null) {
                queryParams.put("page", params.page.toString());
            }
            if (params.limit != null) {
                queryParams.put("limit", params.limit.toString());
            }
            if (params.after != null) {
                queryParams.put("after", params.after);
            }
            if (params.before != null) {
                queryParams.put("before", params.before);
            }
        }

        return client.get("/contacts/" + id + "/activity", queryParams, ContactActivityListResponse.class);
    }
}
