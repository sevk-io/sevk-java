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
        return response != null && response.domains != null ? response.domains : java.util.Collections.emptyList();
    }
}
