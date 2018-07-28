package cd.go.artifact.docker.registry.model;

import com.google.gson.*;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;
import java.util.Optional;

public class ArtifactPlanConfigTypeAdapter implements JsonDeserializer<ArtifactPlanConfig>, JsonSerializer<ArtifactPlanConfig> {

    @Override
    public ArtifactPlanConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (isBuildFileConfig(jsonObject)) {
            return new SourceFileArtifactPlanConfig(jsonObject.get("Source").getAsString());
        } else {
            throw new JsonParseException("Ambiguous or unknown json. `Source` property must be specified.");
        }
    }

    @Override
    public JsonElement serialize(ArtifactPlanConfig src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof SourceFileArtifactPlanConfig) {
            return context.serialize(src, SourceFileArtifactPlanConfig.class);
        }
        throw new JsonIOException("Unknown type of ArtifactPlanConfig");
    }

    private boolean isBuildFileConfig(JsonObject jsonObject) {
        return containsSourceFileProperty(jsonObject);
    }

    private boolean containsSourceFileProperty(JsonObject jsonObject) {
        return jsonObject.has("Source") && isPropertyNotBlank(jsonObject);
    }

    private boolean isPropertyNotBlank(JsonObject jsonObject) {
        try {
            JsonElement jsonElement = jsonObject.get("Source");
            return StringUtils.isNotBlank(jsonElement.getAsString());
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
