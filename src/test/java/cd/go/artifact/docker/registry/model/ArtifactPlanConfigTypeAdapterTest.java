package cd.go.artifact.docker.registry.model;

import com.google.gson.JsonParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.fail;


public class ArtifactPlanConfigTypeAdapterTest {

    @Test
    public void shouldDeserializeToBuildFilePlanConfig() throws JSONException {
        List<String> inputs = Arrays.asList(
                new JSONObject().put("Source", "info.json").toString(),
                new JSONObject().put("Source", "info.json").toString());

        for (String json : inputs) {
            ArtifactPlanConfig artifactPlanConfig = ArtifactPlanConfig.fromJSON(json);

            assertThat(artifactPlanConfig).isInstanceOf(SourceFileArtifactPlanConfig.class);
            assertThat(((SourceFileArtifactPlanConfig) artifactPlanConfig).getSource()).isEqualTo("info.json");
        }
    }

}
