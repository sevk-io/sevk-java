package io.sevk.types;

import java.util.Date;

public class InboundBlocklistEntry {
    public String id;
    public InboundBlocklistType type;
    public String value;
    public String reason;
    public String projectId;
    public Date createdAt;
}
