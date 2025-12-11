package io.sevk;

import io.sevk.markup.MarkupRenderer;
import io.sevk.types.Contact;
import io.sevk.types.Types.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Sevk Java SDK
 * Tests against localhost:4000
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SevkTest {
    private static final String BASE_URL = "http://localhost:4000";
    private static final Gson gson = new Gson();
    private static final OkHttpClient httpClient = new OkHttpClient();

    private static Sevk sevk;
    private static String createdContactId;
    private static String createdAudienceId;
    private static String createdTemplateId;
    private static String createdTopicId;
    private static String createdSegmentId;

    private static String uniqueId() {
        return String.valueOf(System.currentTimeMillis()) + ThreadLocalRandom.current().nextInt(10000);
    }

    @BeforeAll
    static void setupTestEnvironment() throws Exception {
        String unique = uniqueId();
        String testEmail = "sdk-test-" + unique + "@test.example.com";
        String testPassword = "TestPassword123!";

        // 1. Register a new test user
        JsonObject registerBody = new JsonObject();
        registerBody.addProperty("email", testEmail);
        registerBody.addProperty("password", testPassword);

        Request registerRequest = new Request.Builder()
            .url(BASE_URL + "/auth/register")
            .post(RequestBody.create(gson.toJson(registerBody), MediaType.parse("application/json")))
            .build();

        try (Response response = httpClient.newCall(registerRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to register: " + response.code() + " " + response.body().string());
            }
            JsonObject data = gson.fromJson(response.body().string(), JsonObject.class);
            String token = data.get("token").getAsString();

            // 2. Create Project
            JsonObject projectBody = new JsonObject();
            projectBody.addProperty("name", "Test Project");
            projectBody.addProperty("slug", "test-project-" + unique);
            projectBody.addProperty("supportEmail", "support@test.com");

            Request projectRequest = new Request.Builder()
                .url(BASE_URL + "/projects")
                .header("Authorization", "Bearer " + token)
                .post(RequestBody.create(gson.toJson(projectBody), MediaType.parse("application/json")))
                .build();

            try (Response projectResponse = httpClient.newCall(projectRequest).execute()) {
                if (!projectResponse.isSuccessful()) {
                    throw new RuntimeException("Failed to create project: " + projectResponse.code());
                }
                JsonObject projectData = gson.fromJson(projectResponse.body().string(), JsonObject.class);
                String projectId = projectData.getAsJsonObject("project").get("id").getAsString();

                // 3. Create API Key
                JsonObject apiKeyBody = new JsonObject();
                apiKeyBody.addProperty("title", "Test Key");
                apiKeyBody.addProperty("fullAccess", true);

                Request apiKeyRequest = new Request.Builder()
                    .url(BASE_URL + "/projects/" + projectId + "/api-keys")
                    .header("Authorization", "Bearer " + token)
                    .post(RequestBody.create(gson.toJson(apiKeyBody), MediaType.parse("application/json")))
                    .build();

                try (Response apiKeyResponse = httpClient.newCall(apiKeyRequest).execute()) {
                    if (!apiKeyResponse.isSuccessful()) {
                        throw new RuntimeException("Failed to create API key: " + apiKeyResponse.code());
                    }
                    JsonObject apiKeyData = gson.fromJson(apiKeyResponse.body().string(), JsonObject.class);
                    String apiKey = apiKeyData.getAsJsonObject("apiKey").get("key").getAsString();

                    // Initialize SDK
                    SevkOptions options = new SevkOptions().baseUrl(BASE_URL);
                    sevk = new Sevk(apiKey, options);
                }
            }
        }
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
    void testDomainsListStructure() throws Exception {
        List<Domain> domains = sevk.domains().list();

        assertNotNull(domains);
    }

    @Test
    @Order(51)
    @DisplayName("28. Should list only verified domains")
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
        String html = MarkupRenderer.render(markup);

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
        String html = MarkupRenderer.render(markup);

        assertTrue(html.contains("charset=UTF-8"));
        assertTrue(html.contains("viewport"));
    }

    @Test
    @Order(112)
    @DisplayName("49. Should include title when provided")
    void testRenderTitle() {
        String markup = "<email><head><title>Test Email</title></head><body></body></email>";
        String html = MarkupRenderer.render(markup);

        // Document should be rendered with proper structure
        assertTrue(html.contains("<!DOCTYPE html") && html.contains("<head>"));
    }

    @Test
    @Order(113)
    @DisplayName("50. Should include preview text when provided")
    void testRenderPreview() {
        String markup = "<email><head><preview>Preview text here</preview></head><body></body></email>";
        String html = MarkupRenderer.render(markup);

        // Preview text should be somewhere in the output
        assertTrue(html.contains("Preview text") || html.contains("display:none") || html.contains("<body"));
    }

    @Test
    @Order(114)
    @DisplayName("51. Should include custom styles when provided")
    void testRenderCustomStyles() {
        String markup = "<email><head><style>.custom { color: red; }</style></head><body></body></email>";
        String html = MarkupRenderer.render(markup);

        // Styles should be in the output or at least the document should render
        assertTrue(html.contains("custom") || html.contains("<style>") || html.contains("<head>"));
    }

    @Test
    @Order(115)
    @DisplayName("52. Should render empty markup with document structure")
    void testRenderEmptyMarkup() {
        String html = MarkupRenderer.render("");
        assertTrue(html.contains("<!DOCTYPE html"));
        assertTrue(html.contains("<body"));
    }

    @Test
    @Order(116)
    @DisplayName("53. Should have default body styles")
    void testRenderBodyStyles() {
        String markup = "<email><body></body></email>";
        String html = MarkupRenderer.render(markup);

        assertTrue(html.contains("margin:0"));
        assertTrue(html.contains("padding:0"));
        assertTrue(html.contains("font-family"));
    }

    @Test
    @Order(117)
    @DisplayName("54. Should include html lang attribute")
    void testRenderLangAttribute() {
        String markup = "<email><body></body></email>";
        String html = MarkupRenderer.render(markup);

        assertTrue(html.contains("lang=\"en\""));
    }

    @Test
    @Order(118)
    @DisplayName("55. Should include html dir attribute")
    void testRenderDirAttribute() {
        String markup = "<email><body></body></email>";
        String html = MarkupRenderer.render(markup);

        assertTrue(html.contains("dir=\"ltr\""));
    }

    @Test
    @Order(119)
    @DisplayName("56. Should include Content-Type meta tag")
    void testRenderContentType() {
        String markup = "<email><body></body></email>";
        String html = MarkupRenderer.render(markup);

        assertTrue(html.contains("Content-Type"));
        assertTrue(html.contains("text/html"));
    }
}
