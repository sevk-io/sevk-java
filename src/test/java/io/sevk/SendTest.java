package io.sevk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import io.sevk.types.SendEmailRequest;
import io.sevk.types.SendEmailResponse;
import org.junit.jupiter.api.Test;

class SendTest {
    @Test
    void sendsAMarkupEmailThroughTheSevkApi() {
        String apiKey = System.getenv("SEVK_API_KEY");
        String from = System.getenv("SEVK_TEST_FROM");
        String to = System.getenv("SEVK_TEST_TO");
        assumeTrue(apiKey != null && !apiKey.isEmpty() && from != null && !from.isEmpty()
                && to != null && !to.isEmpty(), "SEVK_API_KEY, SEVK_TEST_FROM and SEVK_TEST_TO are required");

        String baseUrl = System.getenv("SEVK_BASE_URL");
        Sevk client = baseUrl == null ? new Sevk(apiKey) : new Sevk(apiKey, new SevkOptions().baseUrl(baseUrl));
        SendEmailResponse response = client.emails().send(new SendEmailRequest()
                .from(from)
                .to(to)
                .subject("Sevk SDK send test")
                .markup("<section><paragraph>Hello from the Sevk Java SDK</paragraph></section>"));

        assertNotNull(response.getId());
        assertFalse(response.getId().isEmpty());
    }
}
