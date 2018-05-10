package cd.go.artifact.docker.registry.model;

import cd.go.artifact.docker.registry.annotation.FieldMetadata;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.Objects;

public class ImageTagArtifactPlanConfig extends ArtifactPlanConfig {

    @Expose
    @SerializedName("Image")
    @FieldMetadata(key = "Image")
    private String image;

    @Expose
    @SerializedName("Tag")
    @FieldMetadata(key = "Tag")
    private String tag;

    @SuppressWarnings("unused")
    public ImageTagArtifactPlanConfig() {
        //GSON
    }

    public ImageTagArtifactPlanConfig(String image, String tag) {
        this.image = image;
        this.tag = tag;
    }

    @Override
    public DockerImage imageToPush(String agentWorkingDirectory, Map<String, String> environmentVariables) {
        String evaluatedTag = evaluate(tag, environmentVariables);
        return new DockerImage(image, evaluatedTag);
    }

    private String evaluate(String tag, Map<String, String> environmentVariables) {
        try {
            return new TagPattern(tag).resolve(environmentVariables);
        } catch (UnresolvedTagException e) {
            return e.getPartiallyResolvedTag(); //TODO bubble up the exception and send a failure response
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageTagArtifactPlanConfig that = (ImageTagArtifactPlanConfig) o;
        return Objects.equals(image, that.image) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, tag);
    }
}
