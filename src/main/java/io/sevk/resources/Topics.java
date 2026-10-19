package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.AddContactsToTopicResponse;
import io.sevk.types.AudienceContactListResponse;
import io.sevk.types.Contact;
import io.sevk.types.CreateTopicRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.ListParams;
import io.sevk.types.ListTopicContactsParams;
import io.sevk.types.RemoveTopicContactResponse;
import io.sevk.types.Topic;
import io.sevk.types.TopicListResponse;
import io.sevk.types.TopicMutationResponse;
import io.sevk.types.UpdateTopicRequest;
import java.util.Collections;
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
     * Lists topics belonging to an audience using default pagination.
     */
    public TopicListResponse list(String audienceId) {
        return list(audienceId, null);
    }

    /**
     * Lists topics belonging to an audience with optional filtering.
     */
    public TopicListResponse list(String audienceId, ListParams params) {
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

        return client.get("/audiences/" + audienceId + "/topics", queryParams, TopicListResponse.class);
    }

    /**
     * Creates a subscription topic under an audience.
     */
    public TopicMutationResponse create(String audienceId, CreateTopicRequest request) {
        return client.post("/audiences/" + audienceId + "/topics", request, TopicMutationResponse.class);
    }

    /**
     * Retrieves a topic by ID.
     */
    public Topic get(String topicId) {
        return client.get("/topics/" + topicId, Topic.class);
    }

    /**
     * Updates a topic while preserving omitted fields.
     */
    public TopicMutationResponse update(String topicId, UpdateTopicRequest request) {
        return client.put("/topics/" + topicId, request, TopicMutationResponse.class);
    }

    /**
     * Deletes a topic without deleting its contacts or parent audience.
     */
    public DeleteResponse delete(String topicId) {
        return client.delete("/topics/" + topicId, DeleteResponse.class);
    }

    /**
     * Adds contacts to a topic, ignoring existing memberships.
     */
    public AddContactsToTopicResponse addContacts(String topicId, List<String> contactIds) {
        Map<String, Object> body = new HashMap<>();

        body.put("contactIds", contactIds);

        return client.post("/topics/" + topicId + "/contacts", body, AddContactsToTopicResponse.class);
    }

    /**
     * Removes a contact from a topic without deleting it.
     */
    public RemoveTopicContactResponse removeContact(String topicId, String contactId) {
        return client.delete("/topics/" + topicId + "/contacts/" + contactId, RemoveTopicContactResponse.class);
    }

    /**
     * Lists contacts attached to a topic using default pagination.
     */
    public AudienceContactListResponse listContacts(String topicId) {
        return listContacts(topicId, null);
    }

    /**
     * Lists contacts attached to a topic with optional filtering.
     */
    public AudienceContactListResponse listContacts(String topicId, ListTopicContactsParams params) {
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
            if (params.subscribed != null) {
                queryParams.put("subscribed", params.subscribed.toString());
            }
        }

        return client.get("/topics/" + topicId + "/contacts", queryParams, AudienceContactListResponse.class);
    }

    /** Returns only contact items from the selected topic page. */
    public List<Contact> listContactItems(String topicId, ListTopicContactsParams params) {
        AudienceContactListResponse response = listContacts(topicId, params);

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }
}
