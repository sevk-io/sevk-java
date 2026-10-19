package io.sevk.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(SendEmailResponse.Adapter.class)
public final class SendEmailResponse {
    private final String id;
    private final List<String> ids;

    private SendEmailResponse(String id, List<String> ids) {
        this.id = id;
        this.ids = ids;
    }

    public static SendEmailResponse single(String id) {
        return new SendEmailResponse(id, null);
    }

    public static SendEmailResponse multiple(List<String> ids) {
        return new SendEmailResponse(null, List.copyOf(ids));
    }

    public boolean isSingle() {
        return id != null;
    }

    public String getId() {
        return id;
    }

    public List<String> getIds() {
        return ids;
    }

    public static final class Adapter extends TypeAdapter<SendEmailResponse> {
        @Override
        public void write(JsonWriter out, SendEmailResponse value) throws IOException {
            out.beginObject();
            if (value.id != null) {
                out.name("id").value(value.id);
            } else {
                out.name("ids").beginArray();
                for (String id : value.ids) {
                    out.value(id);
                }
                out.endArray();
            }
            out.endObject();
        }

        @Override
        public SendEmailResponse read(JsonReader in) throws IOException {
            JsonObject object = JsonParser.parseReader(in).getAsJsonObject();
            boolean hasId = object.has("id");
            boolean hasIds = object.has("ids");
            if (hasId == hasIds) {
                throw new JsonParseException("Expected exactly one of id or ids");
            }
            if (hasId) {
                if (!object.get("id").isJsonPrimitive()
                    || !object.getAsJsonPrimitive("id").isString()) {
                    throw new JsonParseException("Expected id to be a string");
                }
                return SendEmailResponse.single(object.get("id").getAsString());
            }
            if (!object.get("ids").isJsonArray()) {
                throw new JsonParseException("Expected ids to be a string array");
            }
            List<String> ids = new ArrayList<>();
            object.getAsJsonArray("ids").forEach(item -> {
                if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                    throw new JsonParseException("Expected ids to be a string array");
                }
                ids.add(item.getAsString());
            });
            return SendEmailResponse.multiple(ids);
        }
    }
}
