package cd.go.artifact.docker.registry.model;

import cd.go.artifact.docker.registry.annotation.FieldMetadata;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class SourceFileArtifactPlanConfig extends ArtifactPlanConfig {
    @Expose
    @SerializedName("Source")
    @FieldMetadata(key = "Source")
    private String source;

    public SourceFileArtifactPlanConfig(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceFileArtifactPlanConfig that = (SourceFileArtifactPlanConfig) o;
        return Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source);
    }
}
