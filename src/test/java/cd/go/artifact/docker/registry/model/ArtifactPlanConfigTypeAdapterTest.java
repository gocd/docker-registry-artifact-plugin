package cd.go.artifact.docker.registry.model;

import com.google.gson.JsonParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


public class ArtifactPlanConfigTypeAdapterTest {

    @Test
    public void shouldAllowBlankTagAndDefaultToLatest() throws JSONException {
        List<String> inputs = Arrays.asList(
                new JSONObject().put("Image", "alpine").toString(),
                new JSONObject().put("Image", "alpine").put("Tag", "").toString(),
                new JSONObject().put("Image", "alpine").put("Tag", (String) null).toString(),
                new JSONObject().put("Image", "alpine").toString());

        for (String json : inputs) {
            ArtifactPlanConfig artifactPlanConfig = ArtifactPlanConfig.fromJSON(json);

            assertThat(artifactPlanConfig).isInstanceOf(ImageTagArtifactPlanConfig.class);
            assertThat(((ImageTagArtifactPlanConfig) artifactPlanConfig).getImage()).isEqualTo("alpine");
            assertThat(((ImageTagArtifactPlanConfig) artifactPlanConfig).getTag()).isEqualTo("latest");
        }
    }

    @Test
    public void shouldDeserializeToBuildFilePlanConfig() throws JSONException {
        List<String> inputs = Arrays.asList(
                new JSONObject().put("BuildFile", "info.json").put("Tag", "").put("Image", "").toString(),
                new JSONObject().put("BuildFile", "info.json").toString(),
                new JSONObject().put("BuildFile", "info.json").put("Image", (String) null).toString());

        for (String json : inputs) {
            ArtifactPlanConfig artifactPlanConfig = ArtifactPlanConfig.fromJSON(json);

            assertThat(artifactPlanConfig).isInstanceOf(BuildFileArtifactPlanConfig.class);
            assertThat(((BuildFileArtifactPlanConfig) artifactPlanConfig).getBuildFile()).isEqualTo("info.json");
        }
    }

    @Test
    public void shouldThrowAnExceptionWhenBothBuildFileAndImageAreProvided() throws JSONException {
        List<String> inputs = Collections.singletonList(
                new JSONObject().put("BuildFile", "info.json").put("Tag", "").put("Image", "fml").toString());

        for (String json : inputs) {
            try {
                ArtifactPlanConfig artifactPlanConfig = ArtifactPlanConfig.fromJSON(json);
                fail("Should not reach here");
            } catch (JsonParseException e) {
                assertThat(e.getMessage()).isEqualTo("Ambiguous or unknown json. Either `Image` or`BuildFile` property must be specified.");
            }
        }
    }
}
