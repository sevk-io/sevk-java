package io.sevk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class SdkVersion {
    static final String VALUE = load();

    private SdkVersion() {
    }

    private static String load() {
        try (InputStream stream = SdkVersion.class.getResourceAsStream("sevk-version.properties")) {
            if (stream == null) {
                throw new IllegalStateException("sevk-version.properties is missing from the sevk-java build");
            }
            Properties properties = new Properties();
            properties.load(stream);
            String version = properties.getProperty("version");
            if (version == null || version.isEmpty() || version.contains("${")) {
                throw new IllegalStateException("sevk-version.properties was not expanded by the sevk-java build");
            }
            return version;
        } catch (IOException e) {
            throw new IllegalStateException("sevk-version.properties is unreadable", e);
        }
    }
}
