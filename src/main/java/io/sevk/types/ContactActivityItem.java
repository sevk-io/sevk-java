package io.sevk.types;

import com.google.gson.JsonElement;
import java.util.Date;

public class ContactActivityItem {
    public String id;
    public ActivityAction action;
    public String description;
    public Date createdAt;
    public String userAgent;
    public String link;
    public JsonElement metadata;
    public ActivityBroadcastRef broadcast;
    public ActivityEmailRef email;
}
