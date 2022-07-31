/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.artifact.docker.registry.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.MockitoAnnotations.openMocks;

public class BuildFileArtifactPlanConfigTest {
    @TempDir
    public Path tmpFolder;

    private Path agentWorkingDir;
    private final Map<String, String> environmentVariables = new HashMap<>();

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
        agentWorkingDir = Files.createDirectory(tmpFolder.resolve("go-agent"));
    }

    @Test
    public void shouldReadImageAndTagBuildFile() throws IOException, UnresolvedPropertyException {
        Path file = Paths.get(agentWorkingDir.toString(), "build-file.json");
        Files.write(file, "{\"image\":\"alpine\",\"tag\":\"3.6\"}".getBytes());

        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("build-file.json");
        final DockerImage dockerImage = artifactPlanConfig.imageToPush(agentWorkingDir.toString(), environmentVariables);

        assertThat(dockerImage.getImage()).isEqualTo("alpine");
        assertThat(dockerImage.getTag()).isEqualTo("3.6");
    }

    @Test
    public void shouldErrorOutWhenFileContentIsNotAValidJSON() throws IOException {
        Path file = Paths.get(agentWorkingDir.toString(), "build-file.json");
        Files.write(file, "bar".getBytes());
        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("build-file.json");

        assertThatThrownBy(() -> artifactPlanConfig.imageToPush(agentWorkingDir.toString(), environmentVariables))
            .isInstanceOf(RuntimeException.class)
                .hasMessage("File[build-file.json] content is not a valid json. It must contain json data `{'image':'DOCKER-IMAGE-NAME', 'tag':'TAG'}` format.");
    }

    @Test
    public void shouldErrorOutWhenFileContentIsJSONArray() throws IOException {
        Path file = Paths.get(agentWorkingDir.toString(), "build-file.json");
        Files.write(file, "[{}]".getBytes());
        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("build-file.json");

        assertThatThrownBy(() -> artifactPlanConfig.imageToPush(agentWorkingDir.toString(), environmentVariables))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("File[build-file.json] content is not a valid json. It must contain json data `{'image':'DOCKER-IMAGE-NAME', 'tag':'TAG'}` format.");
    }

    @Test
    public void shouldErrorOutWhenFileDoesNotExist() {
        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("random.json");

        assertThatThrownBy(() -> artifactPlanConfig.imageToPush(agentWorkingDir.toString(), environmentVariables))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.format("%s/random.json (No such file or directory)", agentWorkingDir.toString()));
    }
}
