package cd.go.artifact.docker.registry.model;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ArtifactPlanConfigTypeAdapter implements JsonDeserializer<ArtifactPlanConfig>, JsonSerializer<ArtifactPlanConfig> {

    @Override
    public ArtifactPlanConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (isImageTagConfig(jsonObject)) {
            return context.deserialize(json, ImageTagArtifactPlanConfig.class);
        } else if (isBuildFileConfig(jsonObject)) {
            return context.deserialize(json, BuildFileArtifactPlanConfig.class);
        } else {
            throw new JsonParseException("Ambiguous or unknown json");
        }
    }

    @Override
    public JsonElement serialize(ArtifactPlanConfig src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof BuildFileArtifactPlanConfig) {
            return context.serialize(src, BuildFileArtifactPlanConfig.class);
        } else if (src instanceof ImageTagArtifactPlanConfig) {
            return context.serialize(src, ImageTagArtifactPlanConfig.class);
        }
        throw new JsonIOException("Unknown type of ArtifactPlanConfig");
    }

    private boolean isBuildFileConfig(JsonObject jsonObject) {
        return !jsonObject.has("Image") && !jsonObject.has("Tag") && jsonObject.has("BuildFile");
    }

    private boolean isImageTagConfig(JsonObject jsonObject) {
        return jsonObject.has("Image") && jsonObject.has("Tag") && !jsonObject.has("BuildFile");
    }
}
