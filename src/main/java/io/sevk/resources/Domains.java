package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.CreateDomainRequest;
import io.sevk.types.DeleteResponse;
import io.sevk.types.DnsRecordsResponse;
import io.sevk.types.DomainRead;
import io.sevk.types.DomainListResponse;
import io.sevk.types.InboundVerifyResult;
import io.sevk.types.ListDomainsParams;
import io.sevk.types.RegionListResponse;
import io.sevk.types.UpdateDomainRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domains resource for managing email domains.
 */
public class Domains {
    private final SevkClient client;

    public Domains(SevkClient client) {
        this.client = client;
    }

    /**
     * Lists sending domains.
     */
    public DomainListResponse list() {
        return list((ListDomainsParams) null);
    }

    /**
     * Lists sending domains, optionally filtered by verification state.
     */
    public DomainListResponse list(Boolean verified) {
        return list(new ListDomainsParams().verified(verified));
    }

    public DomainListResponse list(ListDomainsParams params) {
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
            if (params.verified != null) {
                queryParams.put("verified", params.verified.toString());
            }
        }

        return client.get("/domains", queryParams, DomainListResponse.class);
    }

    /**
     * Retrieves a domain and its current verification state.
     */
    public DomainRead get(String id) {
        return client.get("/domains/" + id, DomainRead.class);
    }

    /**
     * registers a domain; dns material requires domain:dns-read.
     */
    public DomainRead create(CreateDomainRequest request) {
        return client.post("/domains", request, DomainRead.class);
    }

    /**
     * Updates domain sending, tracking, TLS, or inbound settings.
     */
    public DomainRead update(String id, UpdateDomainRequest request) {
        return client.put("/domains/" + id, request, DomainRead.class);
    }

    /**
     * Deletes a domain after the API confirms it is safe to remove.
     */
    public DeleteResponse delete(String id) {
        return client.delete("/domains/" + id, DeleteResponse.class);
    }

    /**
     * Rechecks sending DNS records and returns the updated domain state.
     */
    public DomainRead verify(String id) {
        return client.post("/domains/" + id + "/verify", null, DomainRead.class);
    }

    /**
     * Checks inbound MX records and returns details useful for DNS diagnosis.
     */
    public InboundVerifyResult verifyInbound(String id) {
        return client.post("/domains/" + id + "/inbound/verify", null, InboundVerifyResult.class);
    }

    /**
     * Returns DNS records required for sending and inbound mail.
     */
    public DnsRecordsResponse dnsRecords(String id) {
        return client.get("/domains/" + id + "/dns-records", DnsRecordsResponse.class);
    }

    /**
     * Lists region identifiers accepted when registering a domain.
     */
    public RegionListResponse regions() {
        return client.get("/domains/regions", RegionListResponse.class);
    }

    /** Returns only region names from the region response. */
    public List<String> regionItems() {
        RegionListResponse response = regions();

        return response != null && response.items != null ? response.items : Collections.emptyList();
    }
}
