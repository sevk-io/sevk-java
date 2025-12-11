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

    public static class SendEmailRequest {
        public String to;
        public String from;
        public String subject;
        public String html;
        public String text;
        public String replyTo;

        public SendEmailRequest to(String to) { this.to = to; return this; }
        public SendEmailRequest from(String from) { this.from = from; return this; }
        public SendEmailRequest subject(String subject) { this.subject = subject; return this; }
        public SendEmailRequest html(String html) { this.html = html; return this; }
        public SendEmailRequest text(String text) { this.text = text; return this; }
        public SendEmailRequest replyTo(String replyTo) { this.replyTo = replyTo; return this; }
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
        public String to;
        public String from;
        public String subject;
        public Date createdAt;
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
        public List<Domain> domains;
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
}
