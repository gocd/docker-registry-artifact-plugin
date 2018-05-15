package cd.go.artifact.docker.registry.annotation;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FieldMetadataTypeAdapter implements JsonSerializer<FieldMetadata> {

    @Override
    public JsonElement serialize(FieldMetadata src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("required", new JsonPrimitive(src.required()));
        jsonObject.add("secure", new JsonPrimitive(src.secure()));
        return jsonObject;
    }
}
