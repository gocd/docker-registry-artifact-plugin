package cd.go.artifact.docker.registry.model;

import com.google.gson.*;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Type;

public class ArtifactPlanConfigTypeAdapter implements JsonDeserializer<ArtifactPlanConfig>, JsonSerializer<ArtifactPlanConfig> {

    @Override
    public ArtifactPlanConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (isImageTagConfig(jsonObject)) {
            return new ImageTagArtifactPlanConfig(jsonObject.get("Image").getAsString(), parseTag(jsonObject));
        } else if (isBuildFileConfig(jsonObject)) {
            return new BuildFileArtifactPlanConfig(jsonObject.get("BuildFile").getAsString());
        } else {
            throw new JsonParseException("Ambiguous or unknown json");
        }
    }

    private String parseTag(JsonObject jsonObject) {
        JsonElement tag = jsonObject.get("Tag");
        if (tag != null && StringUtils.isNotBlank(tag.getAsString())) {
            return tag.getAsString();
        }
        return "latest";
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
        return jsonObject.has("BuildFile") && !jsonObject.has("Image") && !jsonObject.has("Tag");
    }

    private boolean isImageTagConfig(JsonObject jsonObject) {
        return jsonObject.has("Image") && !jsonObject.has("BuildFile");
    }
}
