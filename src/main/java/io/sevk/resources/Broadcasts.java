package io.sevk.resources;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.sevk.SevkClient;
import io.sevk.types.ActiveBroadcastListResponse;
import io.sevk.types.Broadcast;
import io.sevk.types.BroadcastAnalytics;
import io.sevk.types.BroadcastCancelResponse;
import io.sevk.types.BroadcastCostEstimate;
import io.sevk.types.BroadcastEmail;
import io.sevk.types.BroadcastEmailListResponse;
import io.sevk.types.BroadcastListResponse;
import io.sevk.types.BroadcastRecipientEstimate;
import io.sevk.types.BroadcastRecipientEstimateRequest;
import io.sevk.types.BroadcastLifecycleStatus;
import io.sevk.types.BroadcastStatus;
import io.sevk.types.BroadcastSummary;
import io.sevk.types.BroadcastWithVariants;
import io.sevk.types.BroadcastUpdate;
import io.sevk.types.CreateBroadcastRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.ListBroadcastsParams;
import io.sevk.types.ListParams;
import io.sevk.types.PaginationParams;
import io.sevk.types.SendTestRequest;
import io.sevk.types.SendTestResponse;
import io.sevk.types.UpdateBroadcastRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Broadcasts resource for managing email broadcasts.
 */
public class Broadcasts {
    private final SevkClient client;

    public Broadcasts(SevkClient client) {
        this.client = client;
    }

    /**
     * Lists broadcasts using default pagination.
     */
    public BroadcastListResponse list() {
        return list(null);
    }

    /**
     * Lists broadcasts with optional pagination, search, and status filters.
     */
    public BroadcastListResponse list(ListParams params) {
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

            if (params instanceof ListBroadcastsParams) {
                BroadcastLifecycleStatus status = ((ListBroadcastsParams) params).status;

                if (status != null) {
                    queryParams.put("status", status.name());
                }
            }
        }

        return client.get("/broadcasts", queryParams, BroadcastListResponse.class);
    }

    /**
     * Retrieves a broadcast by ID and includes its delivery state.
     */
    public Broadcast get(String id) {
        return client.get("/broadcasts/" + id, Broadcast.class);
    }

    /**
     * Creates a draft or scheduled broadcast for a target group.
     */
    public BroadcastWithVariants create(CreateBroadcastRequest request) {
        return client.post("/broadcasts", request, BroadcastWithVariants.class);
    }

    /**
     * Updates a broadcast while preserving omitted fields.
     */
    public BroadcastUpdate update(String id, UpdateBroadcastRequest request) {
        if (request == null) {
            return client.put("/broadcasts/" + id, null, BroadcastUpdate.class);
        }

        JsonObject body = client.serializeObject(request);

        if (request.isReplyToSet() && request.replyTo == null) {
            body.add("replyTo", JsonNull.INSTANCE);
        }
        if (request.isScheduledAtSet() && request.scheduledAt == null) {
            body.add("scheduledAt", JsonNull.INSTANCE);
        }
        if (request.isOpenTrackingSet() && request.openTracking == null) {
            body.add("openTracking", JsonNull.INSTANCE);
        }
        if (request.isClickTrackingSet() && request.clickTracking == null) {
            body.add("clickTracking", JsonNull.INSTANCE);
        }
        if (request.isVariantsSet() && request.variants == null) {
            body.add("variants", JsonNull.INSTANCE);
        }
        if (request.isAbTestSet() && request.abTest == null) {
            body.add("abTest", JsonNull.INSTANCE);
        }
        if (request.isAudienceIdSet() && request.audienceId == null) {
            body.add("audienceId", JsonNull.INSTANCE);
        }
        if (request.isTopicIdSet() && request.topicId == null) {
            body.add("topicId", JsonNull.INSTANCE);
        }
        if (request.isSegmentIdSet() && request.segmentId == null) {
            body.add("segmentId", JsonNull.INSTANCE);
        }

        return client.put("/broadcasts/" + id, body, BroadcastUpdate.class);
    }

    /**
     * Deletes a broadcast that has not been sent.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/broadcasts/" + id, DeleteResponse.class);
    }

    /**
     * Queues a prepared broadcast for delivery.
     */
    public BroadcastSummary send(String id) {
        return client.post("/broadcasts/" + id + "/send", null, BroadcastSummary.class);
    }

    /**
     * Cancels a broadcast when its state still allows cancellation.
     */
    public BroadcastCancelResponse cancel(String id) {
        return client.post("/broadcasts/" + id + "/cancel", null, BroadcastCancelResponse.class);
    }

    /**
     * Sends a broadcast to test recipients without starting the campaign.
     */
    public SendTestResponse test(String id, SendTestRequest request) {
        return test(id, request, null);
    }

    /**
     * Sends a broadcast to test recipients with optional retry idempotency.
     */
    public SendTestResponse test(String id, SendTestRequest request, TestOptions options) {
        return client.post(
            "/broadcasts/" + id + "/test",
            request,
            SendTestResponse.class,
            idempotencyHeaders(options)
        );
    }

    private static Map<String, String> idempotencyHeaders(TestOptions options) {
        if (options == null || options.idempotencyKey == null) {
            return null;
        }

        return Collections.singletonMap("Idempotency-Key", options.idempotencyKey);
    }

    /** Per-call options for a retry-safe broadcast test send. */
    public static class TestOptions {
        public String idempotencyKey;

        public TestOptions idempotencyKey(String key) {
            this.idempotencyKey = key;
            return this;
        }
    }

    /**
     * Returns aggregate delivery and engagement analytics for a broadcast.
     */
    public BroadcastAnalytics analytics(String id) {
        return client.get("/broadcasts/" + id + "/analytics", BroadcastAnalytics.class);
    }

    /**
     * Returns live scheduling and delivery progress for a broadcast.
     */
    public BroadcastStatus status(String id) {
        return client.get("/broadcasts/" + id + "/status", BroadcastStatus.class);
    }

    /**
     * Lists email deliveries created for a broadcast using default pagination.
     */
    public BroadcastEmailListResponse emails(String id) {
        return emails(id, null);
    }

    /**
     * Lists email deliveries created for a broadcast with pagination.
     */
    public BroadcastEmailListResponse emails(String id, PaginationParams params) {
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

        return client.get("/broadcasts/" + id + "/emails", queryParams, BroadcastEmailListResponse.class);
    }

    /** Returns only email items from the selected broadcast page. */
    public List<BroadcastEmail> emailItems(String id, PaginationParams params) {
        BroadcastEmailListResponse response = emails(id, params);

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }

    /**
     * Estimates delivery cost without sending the broadcast.
     */
    public BroadcastCostEstimate estimateCost(String id) {
        return client.get("/broadcasts/" + id + "/estimate-cost", BroadcastCostEstimate.class);
    }

    /**
     * Resolves the subscribed recipient count for a broadcast target.
     */
    public BroadcastRecipientEstimate estimateRecipients(BroadcastRecipientEstimateRequest request) {
        return client.post("/broadcasts/estimate-recipients", request, BroadcastRecipientEstimate.class);
    }

    /**
     * Lists broadcasts that are currently scheduled or processing.
     */
    public ActiveBroadcastListResponse active() {
        return client.get("/broadcasts/active", ActiveBroadcastListResponse.class);
    }
}
