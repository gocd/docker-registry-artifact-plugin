package cd.go.artifact.docker.registry.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ArtifactPlanConfigTypeAdapterTest {

    @Test
    public void shouldAllowBlankTagAndDefaultToLatest() throws JSONException {
        List<String> inputs = Arrays.asList(
                new JSONObject().put("Image", "alpine").toString(),
                new JSONObject().put("Image", "alpine").put("Tag", "").toString(),
                new JSONObject().put("Image", "alpine").put("Tag", (String) null).toString());

        for (String json : inputs) {
            ArtifactPlanConfig artifactPlanConfig = ArtifactPlanConfig.fromJSON(json);

            assertThat(artifactPlanConfig).isInstanceOf(ImageTagArtifactPlanConfig.class);
            assertThat(((ImageTagArtifactPlanConfig) artifactPlanConfig).getImage()).isEqualTo("alpine");
            assertThat(((ImageTagArtifactPlanConfig) artifactPlanConfig).getTag()).isEqualTo("latest");
        }
    }

}
