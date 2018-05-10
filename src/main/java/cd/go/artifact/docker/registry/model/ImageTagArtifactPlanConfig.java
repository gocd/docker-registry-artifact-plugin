package cd.go.artifact.docker.registry.model;

import cd.go.artifact.docker.registry.annotation.FieldMetadata;
import cd.go.artifact.docker.registry.annotation.ValidationError;
import cd.go.artifact.docker.registry.annotation.ValidationResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

import java.util.*;

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

    @Override
    public ValidationResult validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (StringUtils.isBlank(image)) {
            errors.add(new ValidationError("Image", "Image must not be blank."));
        }
        if (StringUtils.isBlank(tag)) {
            errors.add(new ValidationError("Tag", "Tag must not be blank."));
        }
        return new ValidationResult(errors);
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
