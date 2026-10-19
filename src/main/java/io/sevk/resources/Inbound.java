package io.sevk.resources;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.sevk.SevkClient;
import io.sevk.types.AddInboundBlocklistRequest;
import io.sevk.types.CreateInboundRouteRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.InboundAttachmentDownload;
import io.sevk.types.InboundBlocklistEntry;
import io.sevk.types.InboundBlocklistListResponse;
import io.sevk.types.InboundEmail;
import io.sevk.types.InboundListResponse;
import io.sevk.types.InboundQuota;
import io.sevk.types.InboundRoute;
import io.sevk.types.InboundRouteListResponse;
import io.sevk.types.InboundSecurityConfig;
import io.sevk.types.InboundStats;
import io.sevk.types.ListInboundParams;
import io.sevk.types.PaginationParams;
import io.sevk.types.UpdateInboundQuotaRequest;
import io.sevk.types.UpdateInboundRouteRequest;
import io.sevk.types.UpdateInboundSecurityRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inbound resource. Read incoming mail, manage per-project inbound
 * settings (routes, blocklist, quota, security), and download
 * attachments via short-lived URLs.
 */
public class Inbound {
    private final SevkClient client;

    public Inbound(SevkClient client) {
        this.client = client;
    }

    /**
     * Lists received email using default pagination.
     */
    public InboundListResponse list() {
        return list(null);
    }

    /**
     * Lists received email with optional pagination and filtering.
     */
    public InboundListResponse list(ListInboundParams params) {
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
            if (params.search != null) {
                queryParams.put("search", params.search);
            }
            if (params.status != null) {
                queryParams.put("status", params.status.name());
            }
            if (params.domainId != null) {
                queryParams.put("domainId", params.domainId);
            }
            if (params.broadcastId != null) {
                queryParams.put("broadcastId", params.broadcastId);
            }
            if (params.isSpam != null) {
                queryParams.put("isSpam", params.isSpam.toString());
            }
            if (params.isVirus != null) {
                queryParams.put("isVirus", params.isVirus.toString());
            }
        }

        return client.get("/inbound", queryParams, InboundListResponse.class);
    }

    /**
     * Retrieves a received email with parsed content and attachments.
     */
    public InboundEmail get(String id) {
        return client.get("/inbound/" + id, InboundEmail.class);
    }

    /**
     * Deletes a received email and its stored attachments.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/inbound/" + id, DeleteResponse.class);
    }

    /**
     * Returns aggregate inbound processing statistics.
     */
    public InboundStats stats() {
        return stats(null);
    }

    /**
     * Returns inbound processing statistics scoped to one domain.
     */
    public InboundStats stats(String domainId) {
        Map<String, String> queryParams = new HashMap<>();

        if (domainId != null) {
            queryParams.put("domainId", domainId);
        }

        return client.get("/inbound/stats", queryParams, InboundStats.class);
    }

    /**
     * Creates a short-lived download URL for an inbound attachment.
     */
    public InboundAttachmentDownload downloadAttachment(String inboundEmailId, String attachmentId) {
        return client.get(
                "/inbound/" + inboundEmailId + "/attachments/" + attachmentId + "/download",
                InboundAttachmentDownload.class
        );
    }

    /**
     * Lists rules used to store or forward matching inbound email.
     */
    public InboundRouteListResponse listRoutes() {
        return listRoutes(null);
    }

    /**
     * Lists routing rules using optional offset or cursor pagination.
     */
    public InboundRouteListResponse listRoutes(PaginationParams params) {
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

        return client.get(
                "/inbound-settings/routes",
                queryParams,
                InboundRouteListResponse.class
        );
    }

    /** Returns only routing rule items from the route response. */
    public List<InboundRoute> routeItems() {
        InboundRouteListResponse response = listRoutes();

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }

    /** Returns only routing rule items from one paginated response. */
    public List<InboundRoute> routeItems(PaginationParams params) {
        InboundRouteListResponse response = listRoutes(params);

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }

    /**
     * Retrieves an inbound routing rule by ID.
     */
    public InboundRoute getRoute(String id) {
        return client.get("/inbound-settings/routes/" + id, InboundRoute.class);
    }

    /**
     * Creates an inbound routing rule.
     */
    public InboundRoute createRoute(CreateInboundRouteRequest request) {
        return client.post("/inbound-settings/routes", request, InboundRoute.class);
    }

    /**
     * Updates an inbound routing rule while preserving omitted fields.
     */
    public InboundRoute updateRoute(String id, UpdateInboundRouteRequest request) {
        return client.put("/inbound-settings/routes/" + id, request, InboundRoute.class);
    }

    /**
     * Deletes an inbound routing rule without deleting stored email.
     */
    public DeleteResponse deleteRoute(String id) {
        return client.delete("/inbound-settings/routes/" + id, DeleteResponse.class);
    }

    /**
     * Lists addresses currently blocked from inbound processing.
     */
    public InboundBlocklistListResponse listBlocklist() {
        return listBlocklist(null);
    }

    /**
     * Lists blocked addresses using optional offset or cursor pagination.
     */
    public InboundBlocklistListResponse listBlocklist(PaginationParams params) {
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

        return client.get(
                "/inbound-settings/blocklist",
                queryParams,
                InboundBlocklistListResponse.class
        );
    }

    /** Returns only entries from the inbound blocklist response. */
    public List<InboundBlocklistEntry> blocklistItems() {
        InboundBlocklistListResponse response = listBlocklist();

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }

    /**
     * Adds an address to the inbound blocklist.
     */
    public InboundBlocklistEntry addBlocklistEntry(AddInboundBlocklistRequest request) {
        return client.post("/inbound-settings/blocklist", request, InboundBlocklistEntry.class);
    }

    /**
     * Removes an address from the inbound blocklist.
     */
    public DeleteResponse removeBlocklistEntry(String id) {
        return client.delete("/inbound-settings/blocklist/" + id, DeleteResponse.class);
    }

    /**
     * Returns inbound quota settings and current usage counters.
     */
    public InboundQuota getQuota() {
        return client.get("/inbound-settings/quota", InboundQuota.class);
    }

    /**
     * Updates the inbound monthly limit while preserving omitted fields.
     */
    public InboundQuota updateQuota(UpdateInboundQuotaRequest request) {
        JsonObject body = new JsonObject();

        if (request != null && request.hasMonthlyEmailLimit()) {
            if (request.getMonthlyEmailLimit() == null) {
                body.add("monthlyEmailLimit", JsonNull.INSTANCE);
            } else {
                body.addProperty("monthlyEmailLimit", request.getMonthlyEmailLimit());
            }
        }

        return client.put("/inbound-settings/quota", body, InboundQuota.class);
    }

    /**
     * Clears a quota pause without resetting current-period usage.
     */
    public InboundQuota resetQuota() {
        return client.post("/inbound-settings/quota/reset", null, InboundQuota.class);
    }

    /**
     * Returns effective inbound security settings for a domain.
     */
    public InboundSecurityConfig getSecurityConfig(String domainId) {
        return client.get("/inbound-settings/security/" + domainId, InboundSecurityConfig.class);
    }

    /**
     * Updates inbound security settings for a domain.
     */
    public InboundSecurityConfig updateSecurityConfig(String domainId, UpdateInboundSecurityRequest request) {
        JsonObject body = request == null ? new JsonObject() : client.serializeObject(request);

        if (request != null && request.hasSpamThreshold() && request.getSpamThreshold() == null) {
            body.add("spamThreshold", JsonNull.INSTANCE);
        }

        return client.put("/inbound-settings/security/" + domainId, body, InboundSecurityConfig.class);
    }
}
