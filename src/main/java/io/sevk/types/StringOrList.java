package io.sevk.types;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(StringOrList.Adapter.class)
public final class StringOrList {
    private final String single;
    private final List<String> multiple;

    private StringOrList(String single, List<String> multiple) {
        this.single = single;
        this.multiple = multiple;
    }

    public static StringOrList of(String value) {
        return value == null ? null : new StringOrList(value, null);
    }

    public static StringOrList of(List<String> values) {
        return values == null ? null : new StringOrList(null, new ArrayList<>(values));
    }

    public boolean isSingle() {
        return single != null;
    }

    public String getSingle() {
        return single;
    }

    public List<String> getMultiple() {
        return multiple;
    }

    public static final class Adapter extends TypeAdapter<StringOrList> {
        @Override
        public void write(JsonWriter out, StringOrList value) throws IOException {
            if (value == null || (value.single == null && value.multiple == null)) {
                out.nullValue();
                return;
            }
            if (value.single != null) {
                out.value(value.single);
                return;
            }
            out.beginArray();
            for (String item : value.multiple) {
                out.value(item);
            }
            out.endArray();
        }

        @Override
        public StringOrList read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            if (token == JsonToken.STRING) {
                return StringOrList.of(in.nextString());
            }
            if (token == JsonToken.BEGIN_ARRAY) {
                List<String> values = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    if (in.peek() != JsonToken.STRING) {
                        throw new JsonParseException("Expected a string");
                    }
                    values.add(in.nextString());
                }
                in.endArray();
                return StringOrList.of(values);
            }
            throw new JsonParseException("Expected a string or string array");
        }
    }
}
