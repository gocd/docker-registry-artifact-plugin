package cd.go.artifact.docker.registry.model;

import cd.go.artifact.docker.registry.annotation.FieldMetadata;
import cd.go.artifact.docker.registry.annotation.ValidationError;
import cd.go.artifact.docker.registry.annotation.ValidationResult;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BuildFileArtifactPlanConfig extends ArtifactPlanConfig {
    @Expose
    @SerializedName("BuildFile")
    @FieldMetadata(key = "BuildFile")
    private String buildFile;

    @SuppressWarnings("unused")
    public BuildFileArtifactPlanConfig(){
        //GSON
    }

    public BuildFileArtifactPlanConfig(String buildFile) {
        this.buildFile = buildFile;
    }

    public String getBuildFile() {
        return buildFile;
    }


    @Override
    public DockerImage imageToPush(String agentWorkingDirectory, Map<String, String> environmentVariables) {
        try {
            return DockerImage.fromFile(new File(agentWorkingDirectory, getBuildFile()));
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(String.format("File[%s] content is not a valid json. It must contain json data `{'image':'DOCKER-IMAGE-NAME', 'tag':'TAG'}` format.", buildFile));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ValidationResult validate() {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (StringUtils.isBlank(buildFile)) {
            validationErrors.add(new ValidationError("BuildFile", "BuildFile must not be blank."));
        }
        return new ValidationResult(validationErrors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildFileArtifactPlanConfig that = (BuildFileArtifactPlanConfig) o;
        return Objects.equals(buildFile, that.buildFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildFile);
    }
}
