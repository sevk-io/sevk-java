package io.sevk.types;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Type definitions for Sevk API.
 */
public class Types {

    // ============ Request Types ============

    public static class CreateContactRequest {
        public String email;
        public Boolean subscribed;
        public Map<String, Object> metadata;

        public CreateContactRequest email(String email) { this.email = email; return this; }
        public CreateContactRequest subscribed(Boolean subscribed) { this.subscribed = subscribed; return this; }
        public CreateContactRequest metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
    }

    public static class UpdateContactRequest {
        public Boolean subscribed;
        public Map<String, Object> metadata;

        public UpdateContactRequest subscribed(Boolean subscribed) { this.subscribed = subscribed; return this; }
        public UpdateContactRequest metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
    }

    public static class CreateAudienceRequest {
        public String name;
        public String description;
        public Boolean usersCanSee;

        public CreateAudienceRequest name(String name) { this.name = name; return this; }
        public CreateAudienceRequest description(String description) { this.description = description; return this; }
        public CreateAudienceRequest usersCanSee(Boolean usersCanSee) { this.usersCanSee = usersCanSee; return this; }
    }

    public static class UpdateAudienceRequest {
        public String name;
        public String description;
        public Boolean usersCanSee;

        public UpdateAudienceRequest name(String name) { this.name = name; return this; }
        public UpdateAudienceRequest description(String description) { this.description = description; return this; }
        public UpdateAudienceRequest usersCanSee(Boolean usersCanSee) { this.usersCanSee = usersCanSee; return this; }
    }

    public static class CreateTemplateRequest {
        public String title;
        public String content;

        public CreateTemplateRequest title(String title) { this.title = title; return this; }
        public CreateTemplateRequest content(String content) { this.content = content; return this; }
    }

    public static class UpdateTemplateRequest {
        public String title;
        public String content;

        public UpdateTemplateRequest title(String title) { this.title = title; return this; }
        public UpdateTemplateRequest content(String content) { this.content = content; return this; }
    }

    public static class CreateTopicRequest {
        public String name;
        public String description;

        public CreateTopicRequest name(String name) { this.name = name; return this; }
        public CreateTopicRequest description(String description) { this.description = description; return this; }
    }

    public static class UpdateTopicRequest {
        public String name;
        public String description;

        public UpdateTopicRequest name(String name) { this.name = name; return this; }
        public UpdateTopicRequest description(String description) { this.description = description; return this; }
    }

    public static class SegmentRule {
        public String field;
        public String operator;
        public String value;

        public SegmentRule field(String field) { this.field = field; return this; }
        public SegmentRule operator(String operator) { this.operator = operator; return this; }
        public SegmentRule value(String value) { this.value = value; return this; }
    }

    public static class CreateSegmentRequest {
        public String name;
        public List<SegmentRule> rules;
        public String operator;

        public CreateSegmentRequest name(String name) { this.name = name; return this; }
        public CreateSegmentRequest rules(List<SegmentRule> rules) { this.rules = rules; return this; }
        public CreateSegmentRequest operator(String operator) { this.operator = operator; return this; }
    }

    public static class UpdateSegmentRequest {
        public String name;
        public List<SegmentRule> rules;
        public String operator;

        public UpdateSegmentRequest name(String name) { this.name = name; return this; }
        public UpdateSegmentRequest rules(List<SegmentRule> rules) { this.rules = rules; return this; }
        public UpdateSegmentRequest operator(String operator) { this.operator = operator; return this; }
    }

    public static class SubscribeRequest {
        public String email;
        public String audienceId;
        public List<String> topicIds;

        public SubscribeRequest email(String email) { this.email = email; return this; }
        public SubscribeRequest audienceId(String audienceId) { this.audienceId = audienceId; return this; }
        public SubscribeRequest topicIds(List<String> topicIds) { this.topicIds = topicIds; return this; }
    }

    public static class UnsubscribeRequest {
        public String email;
        public String audienceId;

        public UnsubscribeRequest email(String email) { this.email = email; return this; }
        public UnsubscribeRequest audienceId(String audienceId) { this.audienceId = audienceId; return this; }
    }

    public static class EmailAttachment {
        public String filename;
        public String content;      // Base64 encoded
        public String contentType;  // MIME type

        public EmailAttachment filename(String filename) { this.filename = filename; return this; }
        public EmailAttachment content(String content) { this.content = content; return this; }
        public EmailAttachment contentType(String contentType) { this.contentType = contentType; return this; }
    }

    public static class SendEmailRequest {
        public String to;
        public String from;
        public String subject;
        public String html;
        public String text;
        public String replyTo;
        public List<EmailAttachment> attachments;

        public SendEmailRequest to(String to) { this.to = to; return this; }
        public SendEmailRequest from(String from) { this.from = from; return this; }
        public SendEmailRequest subject(String subject) { this.subject = subject; return this; }
        public SendEmailRequest html(String html) { this.html = html; return this; }
        public SendEmailRequest text(String text) { this.text = text; return this; }
        public SendEmailRequest replyTo(String replyTo) { this.replyTo = replyTo; return this; }
        public SendEmailRequest attachments(List<EmailAttachment> attachments) { this.attachments = attachments; return this; }
    }

    public static class BulkEmailRequest {
        public List<SendEmailRequest> emails;

        public BulkEmailRequest emails(List<SendEmailRequest> emails) { this.emails = emails; return this; }
    }

    public static class CreateBroadcastRequest {
        public String name;
        public String subject;
        public String body;
        public String style;
        public String targetType;
        public String audienceId;
        public String topicId;
        public String segmentId;
        public String senderName;
        public String senderEmail;
        public String domainId;
        public String scheduledAt;

        public CreateBroadcastRequest name(String name) { this.name = name; return this; }
        public CreateBroadcastRequest subject(String subject) { this.subject = subject; return this; }
        public CreateBroadcastRequest body(String body) { this.body = body; return this; }
        public CreateBroadcastRequest style(String style) { this.style = style; return this; }
        public CreateBroadcastRequest targetType(String targetType) { this.targetType = targetType; return this; }
        public CreateBroadcastRequest audienceId(String audienceId) { this.audienceId = audienceId; return this; }
        public CreateBroadcastRequest topicId(String topicId) { this.topicId = topicId; return this; }
        public CreateBroadcastRequest segmentId(String segmentId) { this.segmentId = segmentId; return this; }
        public CreateBroadcastRequest senderName(String senderName) { this.senderName = senderName; return this; }
        public CreateBroadcastRequest senderEmail(String senderEmail) { this.senderEmail = senderEmail; return this; }
        public CreateBroadcastRequest domainId(String domainId) { this.domainId = domainId; return this; }
        public CreateBroadcastRequest scheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; return this; }
    }

    public static class UpdateBroadcastRequest {
        public String name;
        public String subject;
        public String body;
        public String style;
        public String targetType;
        public String audienceId;
        public String topicId;
        public String segmentId;
        public String senderName;
        public String domainId;
        public String scheduledAt;

        public UpdateBroadcastRequest name(String name) { this.name = name; return this; }
        public UpdateBroadcastRequest subject(String subject) { this.subject = subject; return this; }
        public UpdateBroadcastRequest body(String body) { this.body = body; return this; }
        public UpdateBroadcastRequest style(String style) { this.style = style; return this; }
        public UpdateBroadcastRequest targetType(String targetType) { this.targetType = targetType; return this; }
        public UpdateBroadcastRequest audienceId(String audienceId) { this.audienceId = audienceId; return this; }
        public UpdateBroadcastRequest topicId(String topicId) { this.topicId = topicId; return this; }
        public UpdateBroadcastRequest segmentId(String segmentId) { this.segmentId = segmentId; return this; }
        public UpdateBroadcastRequest senderName(String senderName) { this.senderName = senderName; return this; }
        public UpdateBroadcastRequest domainId(String domainId) { this.domainId = domainId; return this; }
        public UpdateBroadcastRequest scheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; return this; }
    }

    public static class SendBroadcastRequest {
        public String scheduledAt;

        public SendBroadcastRequest scheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; return this; }
    }

    public static class SendTestRequest {
        public List<String> emails;

        public SendTestRequest emails(List<String> emails) { this.emails = emails; return this; }
    }

    public static class CreateDomainRequest {
        public String domain;
        public String email;
        public String region;

        public CreateDomainRequest domain(String domain) { this.domain = domain; return this; }
        public CreateDomainRequest email(String email) { this.email = email; return this; }
        public CreateDomainRequest region(String region) { this.region = region; return this; }
    }

    public static class UpdateDomainRequest {
        public String domain;
        public String region;

        public UpdateDomainRequest domain(String domain) { this.domain = domain; return this; }
        public UpdateDomainRequest region(String region) { this.region = region; return this; }
    }

    public static class CreateWebhookRequest {
        public String url;
        public List<String> events;
        public Boolean enabled;

        public CreateWebhookRequest url(String url) { this.url = url; return this; }
        public CreateWebhookRequest events(List<String> events) { this.events = events; return this; }
        public CreateWebhookRequest enabled(Boolean enabled) { this.enabled = enabled; return this; }
    }

    public static class UpdateWebhookRequest {
        public String url;
        public List<String> events;
        public Boolean enabled;

        public UpdateWebhookRequest url(String url) { this.url = url; return this; }
        public UpdateWebhookRequest events(List<String> events) { this.events = events; return this; }
        public UpdateWebhookRequest enabled(Boolean enabled) { this.enabled = enabled; return this; }
    }

    public static class BulkUpdateContactEntry {
        public String email;
        public Boolean subscribed;
        public Map<String, Object> metadata;

        public BulkUpdateContactEntry email(String email) { this.email = email; return this; }
        public BulkUpdateContactEntry subscribed(Boolean subscribed) { this.subscribed = subscribed; return this; }
        public BulkUpdateContactEntry metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
    }

    public static class BulkUpdateContactRequest {
        public List<BulkUpdateContactEntry> contacts;

        public BulkUpdateContactRequest contacts(List<BulkUpdateContactEntry> contacts) { this.contacts = contacts; return this; }
    }

    public static class ImportContactsRequest {
        public List<Map<String, Object>> contacts;
        public String audienceId;

        public ImportContactsRequest contacts(List<Map<String, Object>> contacts) { this.contacts = contacts; return this; }
        public ImportContactsRequest audienceId(String audienceId) { this.audienceId = audienceId; return this; }
    }

    public static class ListParams {
        public Integer page;
        public Integer limit;
        public String search;

        public ListParams page(Integer page) { this.page = page; return this; }
        public ListParams limit(Integer limit) { this.limit = limit; return this; }
        public ListParams search(String search) { this.search = search; return this; }
    }

    // ============ Response Types ============

    public static class Audience {
        public String id;
        public String name;
        public String description;
        public Boolean usersCanSee;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class Template {
        public String id;
        public String title;
        public String content;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class Broadcast {
        public String id;
        public String name;
        public String status;
        public String subject;
        public Date scheduledAt;
        public Date sentAt;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class Domain {
        public String id;
        public String name;
        public Boolean verified;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class Topic {
        public String id;
        public String name;
        public String description;
        public String audienceId;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class Segment {
        public String id;
        public String name;
        public List<SegmentRule> rules;
        public String operator;
        public String audienceId;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class Email {
        public String id;
        public List<String> ids;
        public String to;
        public String from;
        public String subject;
        public Date createdAt;
    }

    public static class BulkEmailError {
        public int index;
        public String email;
        public String error;
    }

    public static class BulkEmailResponse {
        public int success;
        public int failed;
        public List<String> ids;
        public List<BulkEmailError> errors;
    }

    // ============ List Response Types ============

    public static class ListResponse<T> {
        public List<T> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class ContactListResponse {
        public List<io.sevk.types.Contact> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class AudienceListResponse {
        public List<Audience> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class TemplateListResponse {
        public List<Template> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class BroadcastListResponse {
        public List<Broadcast> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class DomainListResponse {
        public List<Domain> items;
    }

    public static class DnsRecord {
        public String type;
        public String name;
        public String value;
        public Integer priority;
        public String status;
    }

    public static class DnsRecordsResponse {
        public List<DnsRecord> items;
    }

    public static class TopicListResponse {
        public List<Topic> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class SegmentListResponse {
        public List<Segment> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class BulkUpdateResponse {
        public int updated;
    }

    public static class ImportContactsResponse {
        public int imported;
        public int failed;
        public List<ImportError> errors;
    }

    public static class ImportError {
        public Integer row;
        public Integer index;
        public String error;
        public String message;
    }

    public static class ContactEvent {
        public String id;
        public String type;
        public String action;
        public String description;
        public Date createdAt;
    }

    public static class ContactEventListResponse {
        public List<ContactEvent> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class AudienceContactListResponse {
        public List<io.sevk.types.Contact> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class RegionListResponse {
        public List<String> items;
    }

    public static class BroadcastAnalytics {
        public Integer total;
        public Integer sent;
        public Integer delivered;
        public Integer opened;
        public Integer clicked;
        public Integer bounced;
        public Integer complained;
    }

    public static class SegmentCalculateResponse {
        public Integer count;
        public List<io.sevk.types.Contact> contacts;
    }

    public static class BroadcastStatus {
        public String id;
        public String name;
        public String status;
        public Integer total;
        public Integer sent;
        public Integer delivered;
        public Integer failed;
    }

    public static class BroadcastEmail {
        public String id;
        public String to;
        public String status;
        public Date createdAt;
    }

    public static class BroadcastEmailListResponse {
        public List<BroadcastEmail> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class BroadcastCostEstimate {
        public Integer recipients;
        public Double estimatedCost;
        public String currency;
    }

    public static class Webhook {
        public String id;
        public String url;
        public List<String> events;
        public Boolean enabled;
        public String secret;
        public Date createdAt;
        public Date updatedAt;
    }

    public static class WebhookListResponse {
        public List<Webhook> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class WebhookTestResponse {
        public Boolean success;
        public String message;
    }

    public static class WebhookEventsResponse {
        public List<String> items;
        public Map<String, WebhookEventDetail> events;
    }

    public static class WebhookEventDetail {
        public String description;
    }

    public static class WebhookEvent {
        public String name;
        public String description;
    }

    public static class Event {
        public String id;
        public String type;
        public String action;
        public String description;
        public Object metadata;
        public Date createdAt;
    }

    public static class EventListResponse {
        public List<Event> items;
        public int total;
        public int page;
        public int totalPages;
    }

    public static class EventStats {
        public int total;
        public int sent;
        public int delivered;
        public int opened;
        public int clicked;
        public int bounced;
        public int complained;
    }
}
