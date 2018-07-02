package cd.go.artifact.docker.registry.model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class EnvironmentVariableResolverTest {

    @Test
    public void shouldResolveTagPatternWithSingleEnvironmentVariable() throws UnresolvedPropertyException {
        EnvironmentVariableResolver environmentVariableResolver = new EnvironmentVariableResolver("v${GO_PIPELINE_COUNTER}", "tag");
        Map<String, String> environmentVariables = ImmutableMap.of("GO_PIPELINE_COUNTER", "112");

        String tag = environmentVariableResolver.resolve(environmentVariables);

        assertThat(tag).isEqualTo("v112");
    }

    @Test
    public void shouldResolveTagPatternWithMultipleEnvironmentVariables() throws UnresolvedPropertyException {
        EnvironmentVariableResolver environmentVariableResolver = new EnvironmentVariableResolver("v${GO_PIPELINE_COUNTER}-${GO_STAGE_COUNTER}", "tag");
        Map<String, String> environmentVariables = ImmutableMap.of("GO_PIPELINE_COUNTER", "112",
                "GO_STAGE_COUNTER", "1");

        String tag = environmentVariableResolver.resolve(environmentVariables);

        assertThat(tag).isEqualTo("v112-1");
    }

    @Test
    public void shouldThrowExceptionIfTagIsUnresolved() {
        EnvironmentVariableResolver environmentVariableResolver = new EnvironmentVariableResolver("v${GO_PIPELINE_COUNTER}-${GO_STAGE_COUNTER}", "tag");
        Map<String, String> environmentVariables = ImmutableMap.of("GO_PIPELINE_COUNTER", "112");

        boolean exceptionCaught = false;
        try {
            environmentVariableResolver.resolve(environmentVariables);
        } catch (UnresolvedPropertyException e) {
            assertThat(e.getPartiallyResolvedTag()).isEqualTo("v112-${GO_STAGE_COUNTER}");
            exceptionCaught = true;
        }

        assertThat(exceptionCaught).isTrue();
    }

}
