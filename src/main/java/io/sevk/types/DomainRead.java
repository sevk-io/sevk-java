package io.sevk.types;

import java.util.List;

/** domain metadata with dns material gated by domain:dns-read. */
public class DomainRead extends DomainSummary {
    public String verificationToken;
    public List<String> dkimTokens;
    public List<DnsRecord> dnsRecords;
}
