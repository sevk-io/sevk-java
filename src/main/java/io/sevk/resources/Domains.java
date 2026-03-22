package io.sevk.resources;

import io.sevk.SevkClient;
import io.sevk.types.Types.*;

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
     * List all domains.
     */
    public List<Domain> list() {
        return list(null);
    }

    /**
     * List domains with optional verified filter.
     */
    public List<Domain> list(Boolean verified) {
        Map<String, String> queryParams = new HashMap<>();
        if (verified != null) {
            queryParams.put("verified", verified.toString());
        }
        DomainListResponse response = client.get("/domains", queryParams, DomainListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }

    /**
     * Get a domain by ID.
     */
    public Domain get(String id) {
        return client.get("/domains/" + id, Domain.class);
    }

    /**
     * Create a new domain.
     */
    public Domain create(CreateDomainRequest request) {
        return client.post("/domains", request, Domain.class);
    }

    /**
     * Update a domain.
     */
    public Domain update(String id, UpdateDomainRequest request) {
        return client.put("/domains/" + id, request, Domain.class);
    }

    /**
     * Delete a domain.
     */
    public void delete(String id) {
        client.delete("/domains/" + id);
    }

    /**
     * Trigger domain verification.
     */
    public Domain verify(String id) {
        return client.post("/domains/" + id + "/verify", null, Domain.class);
    }

    /**
     * Get DNS records for a domain.
     */
    public DnsRecordsResponse getDnsRecords(String id) {
        return client.get("/domains/" + id + "/dns-records", DnsRecordsResponse.class);
    }

    /**
     * Get available regions.
     */
    public List<String> getRegions() {
        RegionListResponse response = client.get("/domains/regions", RegionListResponse.class);
        return response != null && response.items != null ? response.items : java.util.Collections.emptyList();
    }
}
