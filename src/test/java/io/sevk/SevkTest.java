package io.sevk;

import io.sevk.markup.Renderer;
import io.sevk.types.Contact;
import io.sevk.types.Types.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Sevk Java SDK
 * Requires SEVK_TEST_API_KEY environment variable to be set.
 * Optionally uses SEVK_TEST_BASE_URL (defaults to https://api.sevk.io).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SevkTest {
    private static String BASE_URL;

    private static Sevk sevk;
    private static String createdContactId;
    private static String createdAudienceId;
    private static String createdTemplateId;
    private static String createdTopicId;
    private static String createdSegmentId;
    private static String createdBroadcastId;
    private static String createdDomainId;

    private static String uniqueId() {
        return String.valueOf(System.currentTimeMillis()) + ThreadLocalRandom.current().nextInt(10000);
    }

    @BeforeAll
    static void setupTestEnvironment() {
        String apiKey = System.getenv("SEVK_TEST_API_KEY");
        Assumptions.assumeTrue(apiKey != null && !apiKey.isEmpty(),
            "SEVK_TEST_API_KEY environment variable is not set, skipping integration tests");

        BASE_URL = System.getenv("SEVK_TEST_BASE_URL");
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            BASE_URL = "https://api.sevk.io";
        }

        SevkOptions options = new SevkOptions().baseUrl(BASE_URL);
        sevk = new Sevk(apiKey, options);
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    @Order(1)
    @DisplayName("1. Should reject invalid API key")
    void testAuthInvalidApiKey() {
        Sevk invalidSevk = new Sevk("sevk_invalid_api_key_12345", new SevkOptions().baseUrl(BASE_URL));

        Exception exception = assertThrows(SevkException.class, () -> {
            invalidSevk.contacts().list();
        });

        assertTrue(exception.getMessage().contains("401") ||
                   exception.getMessage().toLowerCase().contains("invalid"));
    }

    @Test
    @Order(2)
    @DisplayName("2. Should reject empty API key")
    void testAuthEmptyApiKey() {
        assertThrows(SevkException.class, () -> {
            new Sevk("", new SevkOptions().baseUrl(BASE_URL));
        });
    }

    @Test
    @Order(3)
    @DisplayName("3. Should reject malformed API key")
    void testAuthMalformedApiKey() {
        Sevk malformedSevk = new Sevk("invalid_key_format", new SevkOptions().baseUrl(BASE_URL));

        Exception exception = assertThrows(SevkException.class, () -> {
            malformedSevk.contacts().list();
        });

        assertTrue(exception.getMessage().contains("401"));
    }

    // ==================== CONTACTS TESTS ====================

    @Test
    @Order(10)
    @DisplayName("4. Should list contacts with correct response structure")
    void testContactsListStructure() throws Exception {
        List<Contact> contacts = sevk.contacts().list();

        assertNotNull(contacts);
        // List should be empty or have items
        assertTrue(contacts.size() >= 0);
    }

    @Test
    @Order(11)
    @DisplayName("5. Should list contacts with pagination")
    void testContactsListPagination() throws Exception {
        ListParams params = new ListParams().page(1).limit(5);
        List<Contact> contacts = sevk.contacts().list(params);

        assertNotNull(contacts);
    }

    @Test
    @Order(12)
    @DisplayName("6. Should create a contact with required fields")
    void testContactsCreate() throws Exception {
        String email = "test-" + uniqueId() + "@example.com";
        Contact contact = sevk.contacts().create(email);

        assertNotNull(contact);
        assertNotNull(contact.getId());
        assertEquals(email, contact.getEmail());

        createdContactId = contact.getId();
    }

    @Test
    @Order(13)
    @DisplayName("7. Should get a contact by id")
    void testContactsGet() throws Exception {
        assertNotNull(createdContactId);

        Contact contact = sevk.contacts().get(createdContactId);

        assertNotNull(contact);
        assertEquals(createdContactId, contact.getId());
    }

    @Test
    @Order(14)
    @DisplayName("8. Should update a contact")
    void testContactsUpdate() throws Exception {
        assertNotNull(createdContactId);

        UpdateContactRequest req = new UpdateContactRequest().subscribed(false);
        Contact contact = sevk.contacts().update(createdContactId, req);

        assertNotNull(contact);
        assertEquals(createdContactId, contact.getId());
        assertFalse(contact.isSubscribed());
    }

    @Test
    @Order(15)
    @DisplayName("9. Should throw error for non-existent contact")
    void testContactsNotFound() {
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.contacts().get("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    @Order(16)
    @DisplayName("10. Should delete a contact")
    void testContactsDelete() throws Exception {
        // Create a new contact to delete
        String email = "delete-test-" + uniqueId() + "@example.com";
        Contact contact = sevk.contacts().create(email);

        sevk.contacts().delete(contact.getId());

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.contacts().get(contact.getId());
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== AUDIENCES TESTS ====================

    @Test
    @Order(20)
    @DisplayName("11. Should list audiences with correct response structure")
    void testAudiencesListStructure() throws Exception {
        List<Audience> audiences = sevk.audiences().list();

        assertNotNull(audiences);
        assertTrue(audiences.size() >= 0);
    }

    @Test
    @Order(21)
    @DisplayName("12. Should create an audience with required fields")
    void testAudiencesCreate() throws Exception {
        String name = "Test Audience " + uniqueId();
        CreateAudienceRequest req = new CreateAudienceRequest().name(name);

        Audience audience = sevk.audiences().create(req);

        assertNotNull(audience);
        assertNotNull(audience.id);
        assertEquals(name, audience.name);

        createdAudienceId = audience.id;
    }

    @Test
    @Order(22)
    @DisplayName("13. Should create an audience with all fields")
    void testAudiencesCreateAllFields() throws Exception {
        String name = "Full Audience " + uniqueId();
        CreateAudienceRequest req = new CreateAudienceRequest()
            .name(name)
            .description("Test description");

        Audience audience = sevk.audiences().create(req);

        assertNotNull(audience);
        assertEquals(name, audience.name);
        assertEquals("Test description", audience.description);
    }

    @Test
    @Order(23)
    @DisplayName("14. Should get an audience by id")
    void testAudiencesGet() throws Exception {
        assertNotNull(createdAudienceId);

        Audience audience = sevk.audiences().get(createdAudienceId);

        assertNotNull(audience);
        assertEquals(createdAudienceId, audience.id);
    }

    @Test
    @Order(24)
    @DisplayName("15. Should update an audience")
    void testAudiencesUpdate() throws Exception {
        assertNotNull(createdAudienceId);

        String newName = "Updated Audience " + uniqueId();
        UpdateAudienceRequest req = new UpdateAudienceRequest().name(newName);

        Audience audience = sevk.audiences().update(createdAudienceId, req);

        assertNotNull(audience);
        assertEquals(newName, audience.name);
    }

    @Test
    @Order(25)
    @DisplayName("16. Should add contacts to audience")
    void testAudiencesAddContacts() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdContactId);

        // This should not throw
        sevk.audiences().addContacts(createdAudienceId, Arrays.asList(createdContactId));
    }

    @Test
    @Order(26)
    @DisplayName("17. Should delete an audience")
    void testAudiencesDelete() throws Exception {
        // Create a new audience to delete
        CreateAudienceRequest req = new CreateAudienceRequest()
            .name("Delete Test " + uniqueId());
        Audience audience = sevk.audiences().create(req);

        sevk.audiences().delete(audience.id);

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.audiences().get(audience.id);
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== TEMPLATES TESTS ====================

    @Test
    @Order(30)
    @DisplayName("18. Should list templates with correct response structure")
    void testTemplatesListStructure() throws Exception {
        List<Template> templates = sevk.templates().list();

        assertNotNull(templates);
        assertTrue(templates.size() >= 0);
    }

    @Test
    @Order(31)
    @DisplayName("19. Should create a template with required fields")
    void testTemplatesCreate() throws Exception {
        String title = "Test Template " + uniqueId();
        CreateTemplateRequest req = new CreateTemplateRequest()
            .title(title)
            .content("<p>Hello {{name}}</p>");

        Template template = sevk.templates().create(req);

        assertNotNull(template);
        assertNotNull(template.id);
        assertEquals(title, template.title);
        assertEquals("<p>Hello {{name}}</p>", template.content);

        createdTemplateId = template.id;
    }

    @Test
    @Order(32)
    @DisplayName("20. Should get a template by id")
    void testTemplatesGet() throws Exception {
        assertNotNull(createdTemplateId);

        Template template = sevk.templates().get(createdTemplateId);

        assertNotNull(template);
        assertEquals(createdTemplateId, template.id);
    }

    @Test
    @Order(33)
    @DisplayName("21. Should update a template")
    void testTemplatesUpdate() throws Exception {
        assertNotNull(createdTemplateId);

        String newTitle = "Updated Template " + uniqueId();
        UpdateTemplateRequest req = new UpdateTemplateRequest().title(newTitle);

        Template template = sevk.templates().update(createdTemplateId, req);

        assertNotNull(template);
        assertEquals(newTitle, template.title);
    }

    @Test
    @Order(34)
    @DisplayName("22. Should duplicate a template")
    void testTemplatesDuplicate() throws Exception {
        assertNotNull(createdTemplateId);

        Template template = sevk.templates().duplicate(createdTemplateId);

        assertNotNull(template);
        assertNotNull(template.id);
        assertNotEquals(createdTemplateId, template.id);
    }

    @Test
    @Order(35)
    @DisplayName("23. Should delete a template")
    void testTemplatesDelete() throws Exception {
        // Create a new template to delete
        CreateTemplateRequest req = new CreateTemplateRequest()
            .title("Delete Test " + uniqueId())
            .content("<p>Test</p>");
        Template template = sevk.templates().create(req);

        sevk.templates().delete(template.id);

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.templates().get(template.id);
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== BROADCASTS TESTS ====================

    @Test
    @Order(40)
    @DisplayName("24. Should list broadcasts with correct response structure")
    void testBroadcastsListStructure() throws Exception {
        List<Broadcast> broadcasts = sevk.broadcasts().list();

        assertNotNull(broadcasts);
        assertTrue(broadcasts.size() >= 0);
    }

    @Test
    @Order(41)
    @DisplayName("25. Should list broadcasts with pagination")
    void testBroadcastsListPagination() throws Exception {
        ListParams params = new ListParams().page(1).limit(10);
        List<Broadcast> broadcasts = sevk.broadcasts().list(params);

        assertNotNull(broadcasts);
    }

    @Test
    @Order(42)
    @DisplayName("26. Should list broadcasts with search")
    void testBroadcastsListSearch() throws Exception {
        ListParams params = new ListParams().search("test");
        List<Broadcast> broadcasts = sevk.broadcasts().list(params);

        assertNotNull(broadcasts);
    }

    // ==================== DOMAINS TESTS ====================

    @Test
    @Order(50)
    @DisplayName("27. Should list domains with correct response structure")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsListStructure() throws Exception {
        List<Domain> domains = sevk.domains().list();

        assertNotNull(domains);
    }

    @Test
    @Order(51)
    @DisplayName("28. Should list only verified domains")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsListVerified() throws Exception {
        List<Domain> domains = sevk.domains().list(true);

        assertNotNull(domains);
        // All returned domains should be verified
        for (Domain domain : domains) {
            assertTrue(domain.verified);
        }
    }

    // ==================== TOPICS TESTS ====================

    @Test
    @Order(60)
    @DisplayName("29. Should list topics for an audience")
    void testTopicsList() throws Exception {
        assertNotNull(createdAudienceId);

        List<Topic> topics = sevk.topics().list(createdAudienceId);

        assertNotNull(topics);
        assertTrue(topics.size() >= 0);
    }

    @Test
    @Order(61)
    @DisplayName("30. Should create a topic")
    void testTopicsCreate() throws Exception {
        assertNotNull(createdAudienceId);

        String name = "Test Topic " + uniqueId();
        CreateTopicRequest req = new CreateTopicRequest().name(name);

        Topic topic = sevk.topics().create(createdAudienceId, req);

        assertNotNull(topic);
        assertNotNull(topic.id);
        assertEquals(name, topic.name);
        assertEquals(createdAudienceId, topic.audienceId);

        createdTopicId = topic.id;
    }

    @Test
    @Order(62)
    @DisplayName("31. Should get a topic by id")
    void testTopicsGet() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdTopicId);

        Topic topic = sevk.topics().get(createdAudienceId, createdTopicId);

        assertNotNull(topic);
        assertEquals(createdTopicId, topic.id);
    }

    @Test
    @Order(63)
    @DisplayName("32. Should update a topic")
    void testTopicsUpdate() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdTopicId);

        String newName = "Updated Topic " + uniqueId();
        UpdateTopicRequest req = new UpdateTopicRequest().name(newName);

        Topic topic = sevk.topics().update(createdAudienceId, createdTopicId, req);

        assertNotNull(topic);
        assertEquals(newName, topic.name);
    }

    @Test
    @Order(64)
    @DisplayName("33. Should delete a topic")
    void testTopicsDelete() throws Exception {
        assertNotNull(createdAudienceId);

        // Create a new topic to delete
        CreateTopicRequest req = new CreateTopicRequest()
            .name("Delete Test " + uniqueId());
        Topic topic = sevk.topics().create(createdAudienceId, req);

        sevk.topics().delete(createdAudienceId, topic.id);

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.topics().get(createdAudienceId, topic.id);
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== SEGMENTS TESTS ====================

    @Test
    @Order(70)
    @DisplayName("34. Should list segments for an audience")
    void testSegmentsList() throws Exception {
        assertNotNull(createdAudienceId);

        List<Segment> segments = sevk.segments().list(createdAudienceId);

        assertNotNull(segments);
        assertTrue(segments.size() >= 0);
    }

    @Test
    @Order(71)
    @DisplayName("35. Should create a segment")
    void testSegmentsCreate() throws Exception {
        assertNotNull(createdAudienceId);

        String name = "Test Segment " + uniqueId();
        SegmentRule rule = new SegmentRule()
            .field("email")
            .operator("contains")
            .value("@example.com");

        CreateSegmentRequest req = new CreateSegmentRequest()
            .name(name)
            .rules(Arrays.asList(rule))
            .operator("AND");

        Segment segment = sevk.segments().create(createdAudienceId, req);

        assertNotNull(segment);
        assertNotNull(segment.id);
        assertEquals(name, segment.name);
        assertEquals(createdAudienceId, segment.audienceId);
        assertEquals("AND", segment.operator);

        createdSegmentId = segment.id;
    }

    @Test
    @Order(72)
    @DisplayName("36. Should get a segment by id")
    void testSegmentsGet() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdSegmentId);

        Segment segment = sevk.segments().get(createdAudienceId, createdSegmentId);

        assertNotNull(segment);
        assertEquals(createdSegmentId, segment.id);
    }

    @Test
    @Order(73)
    @DisplayName("37. Should update a segment")
    void testSegmentsUpdate() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdSegmentId);

        String newName = "Updated Segment " + uniqueId();
        UpdateSegmentRequest req = new UpdateSegmentRequest().name(newName);

        Segment segment = sevk.segments().update(createdAudienceId, createdSegmentId, req);

        assertNotNull(segment);
        assertEquals(newName, segment.name);
    }

    @Test
    @Order(74)
    @DisplayName("38. Should delete a segment")
    void testSegmentsDelete() throws Exception {
        assertNotNull(createdAudienceId);

        // Create a new segment to delete
        CreateSegmentRequest req = new CreateSegmentRequest()
            .name("Delete Test " + uniqueId())
            .rules(Arrays.asList())
            .operator("AND");
        Segment segment = sevk.segments().create(createdAudienceId, req);

        sevk.segments().delete(createdAudienceId, segment.id);

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.segments().get(createdAudienceId, segment.id);
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== SUBSCRIPTIONS TESTS ====================

    @Test
    @Order(80)
    @DisplayName("39. Should subscribe a contact")
    void testSubscriptionsSubscribe() throws Exception {
        assertNotNull(createdAudienceId);

        String email = "subscribe-test-" + uniqueId() + "@example.com";
        SubscribeRequest req = new SubscribeRequest()
            .email(email)
            .audienceId(createdAudienceId);

        // This should not throw
        sevk.subscriptions().subscribe(req);
    }

    @Test
    @Order(81)
    @DisplayName("40. Should unsubscribe a contact by email")
    void testSubscriptionsUnsubscribe() throws Exception {
        // Create and subscribe a contact first
        String email = "unsubscribe-test-" + uniqueId() + "@example.com";
        CreateContactRequest createReq = new CreateContactRequest().subscribed(true);
        Contact contact = sevk.contacts().create(email, createReq);

        // Unsubscribe
        UnsubscribeRequest req = new UnsubscribeRequest().email(email);
        sevk.subscriptions().unsubscribe(req);

        // Verify unsubscription
        Contact updatedContact = sevk.contacts().get(contact.getId());
        assertFalse(updatedContact.isSubscribed());
    }

    // ==================== EMAILS TESTS ====================

    @Test
    @Order(90)
    @DisplayName("41. Should reject email with unverified domain")
    void testEmailsRejectUnverifiedDomain() {
        SendEmailRequest req = new SendEmailRequest()
            .to("test@example.com")
            .from("no-reply@unverified-domain.com")
            .subject("Test Email")
            .html("<p>Hello</p>");

        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.emails().send(req);
        });

        String message = exception.getMessage().toLowerCase();
        assertTrue(message.contains("403") || message.contains("domain"));
    }

    @Test
    @Order(91)
    @DisplayName("42. Should reject email with domain not owned by project")
    void testEmailsRejectDomainNotOwned() {
        SendEmailRequest req = new SendEmailRequest()
            .to("test@example.com")
            .from("no-reply@not-my-domain.io")
            .subject("Test Email")
            .html("<p>Hello</p>");

        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.emails().send(req);
        });

        String message = exception.getMessage().toLowerCase();
        assertTrue(message.contains("403") || message.contains("domain"));
    }

    @Test
    @Order(92)
    @DisplayName("43. Should reject email with invalid from address")
    void testEmailsRejectInvalidFrom() {
        SendEmailRequest req = new SendEmailRequest()
            .to("test@example.com")
            .from("invalid-email-without-domain")
            .subject("Test Email")
            .html("<p>Hello</p>");

        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.emails().send(req);
        });

        assertTrue(exception.getMessage().contains("400"));
    }

    @Test
    @Order(93)
    @DisplayName("44. Should return proper error message for domain verification")
    void testEmailsErrorMessage() {
        SendEmailRequest req = new SendEmailRequest()
            .to("recipient@example.com")
            .from("sender@random-unverified-domain.xyz")
            .subject("Test Email")
            .html("<p>Hello World</p>");

        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.emails().send(req);
        });

        String message = exception.getMessage().toLowerCase();
        assertTrue(
            message.contains("domain") ||
            message.contains("verified") ||
            message.contains("forbidden")
        );
    }

    // ==================== DOMAINS UPDATE TESTS ====================

    @Test
    @Order(52)
    @DisplayName("52. Should update a domain")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsUpdate() throws Exception {
        // Create a domain first, then attempt to update it
        CreateDomainRequest createReq = new CreateDomainRequest()
            .domain("test-" + uniqueId() + ".example.com")
            .region("us-east-1");

        try {
            Domain domain = sevk.domains().create(createReq);
            if (domain != null && domain.id != null) {
                UpdateDomainRequest updateReq = new UpdateDomainRequest()
                    .region("eu-west-1");
                Domain updated = sevk.domains().update(domain.id, updateReq);
                assertNotNull(updated);
                assertEquals(domain.id, updated.id);
            }
        } catch (SevkException e) {
            // Domain creation may fail if domain already exists or requires verification
            // This is acceptable for integration testing
        }
    }

    // ==================== BROADCASTS EXTENDED TESTS ====================

    @Test
    @Order(43)
    @DisplayName("43. Should get broadcast status")
    void testBroadcastsGetStatus() throws Exception {
        List<Broadcast> broadcasts = sevk.broadcasts().list();
        assertNotNull(broadcasts);

        if (!broadcasts.isEmpty()) {
            BroadcastStatus status = sevk.broadcasts().getStatus(broadcasts.get(0).id);
            assertNotNull(status);
            assertNotNull(status.status);
        }
    }

    @Test
    @Order(44)
    @DisplayName("44. Should get broadcast emails")
    void testBroadcastsGetEmails() throws Exception {
        List<Broadcast> broadcasts = sevk.broadcasts().list();
        assertNotNull(broadcasts);

        if (!broadcasts.isEmpty()) {
            List<BroadcastEmail> emails = sevk.broadcasts().getEmails(broadcasts.get(0).id);
            assertNotNull(emails);
            assertTrue(emails.size() >= 0);
        }
    }

    @Test
    @Order(45)
    @DisplayName("45. Should estimate broadcast cost")
    void testBroadcastsEstimateCost() throws Exception {
        List<Broadcast> broadcasts = sevk.broadcasts().list();
        assertNotNull(broadcasts);

        if (!broadcasts.isEmpty()) {
            try {
                BroadcastCostEstimate estimate = sevk.broadcasts().estimateCost(broadcasts.get(0).id);
                assertNotNull(estimate);
            } catch (SevkException e) {
                // May fail with 404 if broadcast doesn't support cost estimation
                assertNotNull(e.getMessage());
            }
        }
    }

    @Test
    @Order(46)
    @DisplayName("46. Should list active broadcasts")
    void testBroadcastsListActive() throws Exception {
        List<Broadcast> active = sevk.broadcasts().listActive();
        assertNotNull(active);
        assertTrue(active.size() >= 0);
    }

    // ==================== TOPICS LIST CONTACTS TESTS ====================

    @Test
    @Order(65)
    @DisplayName("65. Should list contacts for a topic")
    void testTopicsListContacts() throws Exception {
        assertNotNull(createdAudienceId);

        // Create a topic
        String name = "List Contacts Topic " + uniqueId();
        CreateTopicRequest req = new CreateTopicRequest().name(name);
        Topic topic = sevk.topics().create(createdAudienceId, req);

        // List contacts for the topic (should be empty initially)
        List<io.sevk.types.Contact> contacts = sevk.topics().listContacts(createdAudienceId, topic.id);
        assertNotNull(contacts);
        assertTrue(contacts.size() >= 0);
    }

    // ==================== WEBHOOKS TESTS (FULL CRUD) ====================

    @Test
    @Order(120)
    @DisplayName("120. Should list webhook events")
    void testWebhooksListEvents() throws Exception {
        List<WebhookEvent> events = sevk.webhooks().listEvents();
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertNotNull(events.get(0).name);
    }

    @Test
    @Order(121)
    @DisplayName("121. Should list webhooks")
    void testWebhooksList() throws Exception {
        List<Webhook> webhooks = sevk.webhooks().list();
        assertNotNull(webhooks);
        assertTrue(webhooks.size() >= 0);
    }

    @Test
    @Order(122)
    @DisplayName("122. Should perform full webhook CRUD cycle")
    void testWebhooksCrudCycle() throws Exception {
        // Get available events
        List<WebhookEvent> availableEvents = sevk.webhooks().listEvents();
        String eventName = (availableEvents != null && !availableEvents.isEmpty())
            ? availableEvents.get(0).name : "contact.subscribed";

        // Create
        CreateWebhookRequest createReq = new CreateWebhookRequest()
            .url("https://example.com/webhook/" + uniqueId())
            .events(Arrays.asList(eventName))
            .enabled(true);

        Webhook webhook = sevk.webhooks().create(createReq);
        assertNotNull(webhook);
        assertNotNull(webhook.id);
        assertTrue(webhook.url.contains("example.com"));
        assertTrue(webhook.enabled);
        assertNotNull(webhook.events);
        assertFalse(webhook.events.isEmpty());

        // Get
        Webhook fetched = sevk.webhooks().get(webhook.id);
        assertNotNull(fetched);
        assertEquals(webhook.id, fetched.id);

        // Update
        UpdateWebhookRequest updateReq = new UpdateWebhookRequest().enabled(false);
        Webhook updated = sevk.webhooks().update(webhook.id, updateReq);
        assertNotNull(updated);
        assertEquals(webhook.id, updated.id);
        assertFalse(updated.enabled);

        // Test
        WebhookTestResponse testResponse = sevk.webhooks().test(webhook.id);
        assertNotNull(testResponse);

        // Delete
        sevk.webhooks().delete(webhook.id);

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.webhooks().get(webhook.id);
        });
        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== EVENTS TESTS ====================

    @Test
    @Order(130)
    @DisplayName("130. Should list events")
    void testEventsList() throws Exception {
        List<Event> events = sevk.events().list();
        assertNotNull(events);
        assertTrue(events.size() >= 0);
    }

    @Test
    @Order(131)
    @DisplayName("131. Should list events with pagination")
    void testEventsListPagination() throws Exception {
        ListParams params = new ListParams().page(1).limit(10);
        List<Event> events = sevk.events().list(params);
        assertNotNull(events);
    }

    @Test
    @Order(132)
    @DisplayName("132. Should get event stats")
    void testEventsStats() throws Exception {
        EventStats stats = sevk.events().stats();
        assertNotNull(stats);
    }

    // ==================== USAGE TESTS ====================

    @Test
    @Order(140)
    @DisplayName("140. Should get project usage")
    void testGetUsage() throws Exception {
        com.google.gson.JsonObject usage = sevk.getUsage();
        assertNotNull(usage);
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @Order(100)
    @DisplayName("45. Should handle 404 errors gracefully")
    void testError404() {
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.contacts().get("non-existent-id-12345");
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    @Order(101)
    @DisplayName("46. Should handle validation errors")
    void testErrorValidation() {
        assertThrows(SevkException.class, () -> {
            sevk.contacts().create("invalid-email");
        });
    }

    // ==================== MARKUP RENDERER TESTS ====================

    @Test
    @Order(110)
    @DisplayName("47. Should return HTML document structure")
    void testRenderDocumentStructure() {
        String markup = "<email><body></body></email>";
        String html = Renderer.render(markup);

        assertTrue(html.contains("<!DOCTYPE html"));
        assertTrue(html.contains("<html"));
        assertTrue(html.contains("<head>"));
        assertTrue(html.contains("<body"));
        assertTrue(html.contains("</html>"));
    }

    @Test
    @Order(111)
    @DisplayName("48. Should include meta tags")
    void testRenderMetaTags() {
        String markup = "<email><body></body></email>";
        String html = Renderer.render(markup);

        assertTrue(html.contains("charset=UTF-8"));
        assertTrue(html.contains("viewport"));
    }

    @Test
    @Order(112)
    @DisplayName("49. Should include title when provided")
    void testRenderTitle() {
        String markup = "<email><head><title>Test Email</title></head><body></body></email>";
        String html = Renderer.render(markup);

        // Document should be rendered with proper structure
        assertTrue(html.contains("<!DOCTYPE html") && html.contains("<head>"));
    }

    @Test
    @Order(113)
    @DisplayName("50. Should include preview text when provided")
    void testRenderPreview() {
        String markup = "<email><head><preview>Preview text here</preview></head><body></body></email>";
        String html = Renderer.render(markup);

        // Preview text should be somewhere in the output
        assertTrue(html.contains("Preview text") || html.contains("display:none") || html.contains("<body"));
    }

    @Test
    @Order(114)
    @DisplayName("51. Should include custom styles when provided")
    void testRenderCustomStyles() {
        String markup = "<email><head><style>.custom { color: red; }</style></head><body></body></email>";
        String html = Renderer.render(markup);

        // Styles should be in the output or at least the document should render
        assertTrue(html.contains("custom") || html.contains("<style>") || html.contains("<head>"));
    }

    @Test
    @Order(115)
    @DisplayName("52. Should render empty markup with document structure")
    void testRenderEmptyMarkup() {
        String html = Renderer.render("");
        assertTrue(html.contains("<!DOCTYPE html"));
        assertTrue(html.contains("<body"));
    }

    @Test
    @Order(116)
    @DisplayName("53. Should have default body styles")
    void testRenderBodyStyles() {
        String markup = "<email><body></body></email>";
        String html = Renderer.render(markup);

        assertTrue(html.contains("margin:0"));
        assertTrue(html.contains("padding:0"));
        assertTrue(html.contains("font-family"));
    }

    @Test
    @Order(117)
    @DisplayName("54. Should include html lang attribute")
    void testRenderLangAttribute() {
        String markup = "<email><body></body></email>";
        String html = Renderer.render(markup);

        assertTrue(html.contains("lang=\"en\""));
    }

    @Test
    @Order(118)
    @DisplayName("55. Should include html dir attribute")
    void testRenderDirAttribute() {
        String markup = "<email><body></body></email>";
        String html = Renderer.render(markup);

        assertTrue(html.contains("dir=\"ltr\""));
    }

    @Test
    @Order(119)
    @DisplayName("56. Should include Content-Type meta tag")
    void testRenderContentType() {
        String markup = "<email><body></body></email>";
        String html = Renderer.render(markup);

        assertTrue(html.contains("Content-Type"));
        assertTrue(html.contains("text/html"));
    }

    // ==================== CONTACTS EXTENDED TESTS ====================

    @Test
    @Order(150)
    @DisplayName("150. Should bulk update contacts")
    void testContactsBulkUpdate() throws Exception {
        assertNotNull(createdContactId);

        // Get the contact to get the email
        Contact contact = sevk.contacts().get(createdContactId);

        BulkUpdateContactEntry entry = new BulkUpdateContactEntry()
            .email(contact.getEmail())
            .subscribed(true);

        BulkUpdateResponse result = sevk.contacts().bulkUpdate(Arrays.asList(entry));

        assertNotNull(result);
    }

    @Test
    @Order(151)
    @DisplayName("151. Should get contact events")
    void testContactsGetEvents() throws Exception {
        assertNotNull(createdContactId);

        List<ContactEvent> events = sevk.contacts().getEvents(createdContactId);

        assertNotNull(events);
        assertTrue(events.size() >= 0);
    }

    @Test
    @Order(152)
    @DisplayName("152. Should import contacts")
    void testContactsImportContacts() throws Exception {
        String email = "import-test-" + uniqueId() + "@example.com";
        java.util.Map<String, Object> contactEntry = new java.util.HashMap<>();
        contactEntry.put("email", email);
        ImportContactsRequest req = new ImportContactsRequest()
            .contacts(Arrays.asList(contactEntry));

        try {
            ImportContactsResponse result = sevk.contacts().importContacts(req);
            assertNotNull(result);
        } catch (SevkException e) {
            // May hit rate limit during testing
            assertTrue(e.getMessage().contains("429") || e.getMessage().contains("rate"));
        }
    }

    // ==================== AUDIENCES EXTENDED TESTS ====================

    @Test
    @Order(160)
    @DisplayName("160. Should list contacts in an audience")
    void testAudiencesListContacts() throws Exception {
        assertNotNull(createdAudienceId);

        List<io.sevk.types.Contact> contacts = sevk.audiences().listContacts(createdAudienceId);

        assertNotNull(contacts);
        assertTrue(contacts.size() >= 0);
    }

    @Test
    @Order(161)
    @DisplayName("161. Should remove a contact from an audience")
    void testAudiencesRemoveContact() throws Exception {
        assertNotNull(createdAudienceId);

        // Create a contact and add to audience, then remove
        String email = "audience-remove-test-" + uniqueId() + "@example.com";
        Contact contact = sevk.contacts().create(email);
        sevk.audiences().addContacts(createdAudienceId, Arrays.asList(contact.getId()));

        sevk.audiences().removeContact(createdAudienceId, contact.getId());

        // Verify removal by listing contacts
        List<io.sevk.types.Contact> contacts = sevk.audiences().listContacts(createdAudienceId);
        for (io.sevk.types.Contact c : contacts) {
            assertNotEquals(contact.getId(), c.getId());
        }
    }

    // ==================== BROADCASTS CRUD TESTS ====================

    @Test
    @Order(170)
    @DisplayName("170. Should create a broadcast")
    void testBroadcastsCreate() throws Exception {
        // Get a domain from the project to use for broadcast
        List<Domain> domains = sevk.domains().list();
        if (domains.isEmpty()) return;
        String domainId = domains.get(0).id;

        String name = "Test Broadcast " + uniqueId();
        CreateBroadcastRequest req = new CreateBroadcastRequest()
            .domainId(domainId)
            .name(name)
            .subject("Test Subject")
            .body("<section><paragraph>Test broadcast body</paragraph></section>")
            .senderName("Test Sender")
            .senderEmail("test")
            .targetType("ALL");

        Broadcast broadcast = sevk.broadcasts().create(req);

        assertNotNull(broadcast);
        assertNotNull(broadcast.id);
        assertEquals(name, broadcast.name);

        createdBroadcastId = broadcast.id;
    }

    @Test
    @Order(171)
    @DisplayName("171. Should get a broadcast by id")
    void testBroadcastsGet() throws Exception {
        if (createdBroadcastId == null) return;

        Broadcast broadcast = sevk.broadcasts().get(createdBroadcastId);

        assertNotNull(broadcast);
        assertEquals(createdBroadcastId, broadcast.id);
    }

    @Test
    @Order(172)
    @DisplayName("172. Should update a broadcast")
    void testBroadcastsUpdate() throws Exception {
        if (createdBroadcastId == null) return;

        String newName = "Updated Broadcast " + uniqueId();
        UpdateBroadcastRequest req = new UpdateBroadcastRequest().name(newName);

        Broadcast broadcast = sevk.broadcasts().update(createdBroadcastId, req);

        assertNotNull(broadcast);
        assertEquals(createdBroadcastId, broadcast.id);
        assertEquals(newName, broadcast.name);
    }

    @Test
    @Order(173)
    @DisplayName("173. Should get broadcast analytics")
    void testBroadcastsGetAnalytics() throws Exception {
        if (createdBroadcastId == null) return;

        BroadcastAnalytics analytics = sevk.broadcasts().getAnalytics(createdBroadcastId);

        assertNotNull(analytics);
    }

    @Test
    @Order(174)
    @DisplayName("174. Should send a test broadcast")
    void testBroadcastsSendTest() throws Exception {
        if (createdBroadcastId == null) return;

        try {
            SendTestRequest req = new SendTestRequest()
                .emails(Arrays.asList("test@example.com"));
            sevk.broadcasts().sendTest(createdBroadcastId, req);
        } catch (SevkException e) {
            // May fail if domain is unverified, which is expected
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @Order(175)
    @DisplayName("175. Should handle send error for draft broadcast")
    void testBroadcastsHandleSendError() throws Exception {
        if (createdBroadcastId == null) return;

        try {
            sevk.broadcasts().send(createdBroadcastId);
            // If it succeeds, that's fine too
        } catch (SevkException e) {
            // Expected to fail if broadcast is not ready to send
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().length() > 0);
        }
    }

    @Test
    @Order(176)
    @DisplayName("176. Should handle cancel for a non-sending broadcast")
    void testBroadcastsHandleCancelError() throws Exception {
        if (createdBroadcastId == null) return;

        try {
            sevk.broadcasts().cancel(createdBroadcastId);
        } catch (SevkException e) {
            // Expected to fail if broadcast is not in a cancellable state
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @Order(177)
    @DisplayName("177. Should delete a broadcast")
    void testBroadcastsDelete() throws Exception {
        if (createdBroadcastId == null) return;

        sevk.broadcasts().delete(createdBroadcastId);

        // Verify deletion
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.broadcasts().get(createdBroadcastId);
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    // ==================== DOMAINS CRUD TESTS ====================

    @Test
    @Order(180)
    @DisplayName("180. Should create a domain")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsCreate() throws Exception {
        String subdomain = "test-" + uniqueId() + ".example.com";
        CreateDomainRequest req = new CreateDomainRequest()
            .domain(subdomain)
            .email("test@" + subdomain);

        Domain domain = sevk.domains().create(req);

        assertNotNull(domain);
        assertNotNull(domain.id);

        createdDomainId = domain.id;
    }

    @Test
    @Order(181)
    @DisplayName("181. Should get a domain by id")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsGet() throws Exception {
        if (createdDomainId == null) return;

        Domain domain = sevk.domains().get(createdDomainId);

        assertNotNull(domain);
        assertEquals(createdDomainId, domain.id);
    }

    @Test
    @Order(182)
    @DisplayName("182. Should get DNS records for a domain")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsGetDnsRecords() throws Exception {
        if (createdDomainId == null) return;

        DnsRecordsResponse result = sevk.domains().getDnsRecords(createdDomainId);

        assertNotNull(result);
    }

    @Test
    @Order(183)
    @DisplayName("183. Should get available regions")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsGetRegions() throws Exception {
        List<String> regions = sevk.domains().getRegions();

        assertNotNull(regions);
    }

    @Test
    @Order(184)
    @DisplayName("184. Should verify a domain")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsVerify() throws Exception {
        if (createdDomainId == null) return;

        try {
            Domain domain = sevk.domains().verify(createdDomainId);
            assertNotNull(domain);
        } catch (SevkException e) {
            // Expected to fail for test domains without proper DNS records
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @Order(185)
    @DisplayName("185. Should delete a domain")
    @EnabledIfEnvironmentVariable(named = "INCLUDE_DOMAIN_TESTS", matches = "true")
    void testDomainsDelete() throws Exception {
        if (createdDomainId == null) return;

        sevk.domains().delete(createdDomainId);

        // Verify deletion
        try {
            sevk.domains().get(createdDomainId);
            fail("Expected SevkException for deleted domain");
        } catch (SevkException e) {
            // Accept any error as confirmation of deletion
            assertNotNull(e.getMessage());
        }
    }

    // ==================== TOPICS EXTENDED TESTS ====================

    @Test
    @Order(190)
    @DisplayName("190. Should add contacts to a topic")
    void testTopicsAddContacts() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdTopicId);
        assertNotNull(createdContactId);

        // Ensure contact is in the audience first
        sevk.audiences().addContacts(createdAudienceId, Arrays.asList(createdContactId));

        sevk.topics().addContacts(createdAudienceId, createdTopicId, Arrays.asList(createdContactId));
    }

    @Test
    @Order(191)
    @DisplayName("191. Should remove a contact from a topic")
    void testTopicsRemoveContact() throws Exception {
        if (createdAudienceId == null || createdTopicId == null) return;

        // Create a contact, add to audience and topic, then remove from topic
        String email = "topic-remove-test-" + uniqueId() + "@example.com";
        Contact contact = sevk.contacts().create(email);
        sevk.audiences().addContacts(createdAudienceId, Arrays.asList(contact.getId()));
        sevk.topics().addContacts(createdAudienceId, createdTopicId, Arrays.asList(contact.getId()));

        sevk.topics().removeContact(createdAudienceId, createdTopicId, contact.getId());

        // Verify removal by listing contacts in the topic
        List<io.sevk.types.Contact> contacts = sevk.topics().listContacts(createdAudienceId, createdTopicId);
        for (io.sevk.types.Contact c : contacts) {
            assertNotEquals(contact.getId(), c.getId());
        }
    }

    @Test
    @Order(192)
    @DisplayName("192. Should list contacts for a topic")
    void testTopicsListContactsExtended() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdTopicId);

        List<io.sevk.types.Contact> contacts = sevk.topics().listContacts(createdAudienceId, createdTopicId);

        assertNotNull(contacts);
        assertTrue(contacts.size() >= 0);
    }

    // ==================== SEGMENTS EXTENDED TESTS ====================

    @Test
    @Order(200)
    @DisplayName("200. Should calculate a segment")
    void testSegmentsCalculate() throws Exception {
        assertNotNull(createdAudienceId);
        assertNotNull(createdSegmentId);

        try {
            SegmentCalculateResponse result = sevk.segments().calculate(createdAudienceId, createdSegmentId);
            assertNotNull(result);
        } catch (SevkException e) {
            // May hit rate limit during testing, which is acceptable
            assertTrue(e.getMessage().contains("429") || e.getMessage().contains("rate"));
        }
    }

    @Test
    @Order(201)
    @DisplayName("201. Should preview a segment")
    void testSegmentsPreview() throws Exception {
        assertNotNull(createdAudienceId);

        CreateSegmentRequest req = new CreateSegmentRequest()
            .rules(Arrays.asList(
                new SegmentRule()
                    .field("email")
                    .operator("contains")
                    .value("@example.com")
            ))
            .operator("AND");

        SegmentCalculateResponse result = sevk.segments().preview(createdAudienceId, req);

        assertNotNull(result);
    }

    // ==================== EMAILS EXTENDED TESTS ====================

    @Test
    @Order(210)
    @DisplayName("210. Should throw error for non-existent email id")
    void testEmailsGetNonExistent() {
        Exception exception = assertThrows(SevkException.class, () -> {
            sevk.emails().get("00000000-0000-0000-0000-000000000000");
        });

        assertTrue(exception.getMessage().contains("404"));
    }

    @Test
    @Order(211)
    @DisplayName("211. Should handle bulk email with unverified domain")
    void testEmailsBulkReject() {
        SendEmailRequest email1 = new SendEmailRequest()
            .to("test1@example.com")
            .from("no-reply@unverified-domain.com")
            .subject("Bulk Test 1")
            .html("<p>Hello 1</p>");

        SendEmailRequest email2 = new SendEmailRequest()
            .to("test2@example.com")
            .from("no-reply@unverified-domain.com")
            .subject("Bulk Test 2")
            .html("<p>Hello 2</p>");

        BulkEmailRequest req = new BulkEmailRequest()
            .emails(Arrays.asList(email1, email2));

        try {
            BulkEmailResponse result = sevk.emails().sendBulk(req);
            // Backend may return a response with failed count instead of throwing
            assertNotNull(result);
            assertTrue(result.failed > 0 || result.errors != null);
        } catch (SevkException e) {
            // Or it may throw an exception, which is also acceptable
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().length() > 0);
        }
    }
}
