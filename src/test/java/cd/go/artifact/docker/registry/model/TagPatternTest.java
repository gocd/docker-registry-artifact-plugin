package cd.go.artifact.docker.registry.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class TagPatternTest {

    @Test
    public void shouldResolveTagPatternWithSingleEnvironmentVariable() throws UnresolvedTagException {
        TagPattern tagPattern = new TagPattern("v${GO_PIPELINE_COUNTER}");
        Map<String, String> environmentVariables = ImmutableMap.of("GO_PIPELINE_COUNTER", "112");

        String tag = tagPattern.resolve(environmentVariables);

        assertThat(tag).isEqualTo("v112");
    }

    @Test
    public void shouldResolveTagPatternWithMultipleEnvironmentVariables() throws UnresolvedTagException {
        TagPattern tagPattern = new TagPattern("v${GO_PIPELINE_COUNTER}-${GO_STAGE_COUNTER}");
        Map<String, String> environmentVariables = ImmutableMap.of("GO_PIPELINE_COUNTER", "112",
                "GO_STAGE_COUNTER", "1");

        String tag = tagPattern.resolve(environmentVariables);

        assertThat(tag).isEqualTo("v112-1");
    }

    @Test
    public void shouldThrowExceptionIfTagIsUnresolved() {
        TagPattern tagPattern = new TagPattern("v${GO_PIPELINE_COUNTER}-${GO_STAGE_COUNTER}");
        Map<String, String> environmentVariables = ImmutableMap.of("GO_PIPELINE_COUNTER", "112");

        boolean exceptionCaught = false;
        try {
            tagPattern.resolve(environmentVariables);
        } catch (UnresolvedTagException e) {
            assertThat(e.getPartiallyResolvedTag()).isEqualTo("v112-${GO_STAGE_COUNTER}");
            exceptionCaught = true;
        }

        assertThat(exceptionCaught).isTrue();
    }

}
