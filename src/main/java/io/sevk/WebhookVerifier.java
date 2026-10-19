package io.sevk;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifier for sevk webhook deliveries.
 *
 * sevk uses the Svix-compatible signature scheme ({@code svix-id},
 * {@code svix-timestamp}, {@code svix-signature} headers), so this helper or
 * the official {@code svix} Java SDK can be used. sevk issues canonical
 * {@code whsec_} base64 secrets that can be passed directly to either verifier.
 */
public class WebhookVerifier {
    private static final long TOLERANCE_SECONDS = 5 * 60;
    private static final Pattern CANONICAL_BASE64 =
            Pattern.compile("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");

    private final byte[] key;

    /** Prepares a verifier with the signing secret issued for a webhook. */
    public WebhookVerifier(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new WebhookVerificationException("Webhook secret is required");
        }

        if (secret.startsWith("whsec_")) {
            String encoded = secret.substring("whsec_".length());
            if (!CANONICAL_BASE64.matcher(encoded).matches()) {
                throw new WebhookVerificationException("Invalid base64 secret");
            }
            try {
                this.key = Base64.getDecoder().decode(encoded);
                if (this.key.length == 0) {
                    throw new WebhookVerificationException("Invalid base64 secret");
                }
            } catch (IllegalArgumentException e) {
                throw new WebhookVerificationException("Invalid base64 secret", e);
            }
        } else {
            this.key = secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Verifies the delivery signature and timestamp, then parses its JSON payload.
     *
     * @throws WebhookVerificationException on missing headers, expired timestamp, or signature mismatch.
     */
    public JsonElement verify(String payload, Map<String, String> headers) {
        String msgId = lookup(headers, "svix-id");
        String msgTimestamp = lookup(headers, "svix-timestamp");
        String msgSignature = lookup(headers, "svix-signature");

        if (msgId == null || msgId.isEmpty() || msgTimestamp == null || msgSignature == null) {
            throw new WebhookVerificationException("Missing required svix headers");
        }

        long timestamp;
        if (!isAsciiDigits(msgTimestamp)) {
            throw new WebhookVerificationException("Invalid timestamp header");
        }
        try {
            timestamp = Long.parseLong(msgTimestamp);
        } catch (NumberFormatException e) {
            throw new WebhookVerificationException("Invalid timestamp header", e);
        }

        long now = Instant.now().getEpochSecond();

        if (timestamp < now - TOLERANCE_SECONDS || timestamp > now + TOLERANCE_SECONDS) {
            throw new WebhookVerificationException("Timestamp outside tolerance window");
        }

        String toSign = msgId + "." + msgTimestamp + "." + payload;
        String expected = "v1," + Base64.getEncoder().encodeToString(hmacSha256(toSign));
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);

        for (String candidate : msgSignature.split(" ")) {
            byte[] candidateBytes = candidate.getBytes(StandardCharsets.UTF_8);
            if (constantTimeEquals(candidateBytes, expectedBytes)) {
                try {
                    return JsonParser.parseString(payload);
                } catch (JsonSyntaxException e) {
                    throw new WebhookVerificationException("Invalid JSON payload", e);
                }
            }
        }

        throw new WebhookVerificationException("Signature mismatch");
    }

    private byte[] hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(this.key, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new WebhookVerificationException("HMAC SHA256 unavailable", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }

        return result == 0;
    }

    private static boolean isAsciiDigits(String value) {
        if (value.isEmpty()) return false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (current < '0' || current > '9') return false;
        }
        return true;
    }

    private static String lookup(Map<String, String> headers, String name) {
        for (Map.Entry<String, String> e : headers.entrySet()) {
            String normalized = e.getKey().replace('_', '-');
            if (normalized.regionMatches(true, 0, "HTTP-", 0, 5)) {
                normalized = normalized.substring(5);
            }
            if (normalized.equalsIgnoreCase(name) && e.getValue() != null) {
                return e.getValue();
            }
        }

        return null;
    }
}
