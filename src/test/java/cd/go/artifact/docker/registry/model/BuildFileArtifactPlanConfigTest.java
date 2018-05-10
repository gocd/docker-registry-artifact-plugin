/*
 * Copyright 2018 ThoughtWorks, Inc.
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class BuildFileArtifactPlanConfigTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File agentWorkingDir;
    private final Map<String, String> environmentVariables = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        agentWorkingDir = tmpFolder.newFolder("go-agent");
    }

    @Test
    public void shouldReadImageAndTagBuildFile() throws IOException {
        Path file = Paths.get(agentWorkingDir.getAbsolutePath(), "build-file.json");
        Files.write(file, "{\"image\":\"alpine\",\"tag\":\"3.6\"}".getBytes());

        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("build-file.json");
        final DockerImage dockerImage = artifactPlanConfig.imageToPush(agentWorkingDir.getAbsolutePath(), environmentVariables);

        assertThat(dockerImage.getImage()).isEqualTo("alpine");
        assertThat(dockerImage.getTag()).isEqualTo("3.6");
    }

    @Test
    public void shouldErrorOutWhenFileContentIsNotAValidJSON() throws IOException {
        Path file = Paths.get(agentWorkingDir.getAbsolutePath(), "build-file.json");
        Files.write(file, "bar".getBytes());
        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("build-file.json");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("File[build-file.json] content is not a valid json. It must contain json data `{'image':'DOCKER-IMAGE-NAME', 'tag':'TAG'}` format.");

        artifactPlanConfig.imageToPush(agentWorkingDir.getAbsolutePath(), environmentVariables);
    }

    @Test
    public void shouldErrorOutWhenFileContentIsJSONArray() throws IOException {
        Path file = Paths.get(agentWorkingDir.getAbsolutePath(), "build-file.json");
        Files.write(file, "[{}]".getBytes());
        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("build-file.json");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("File[build-file.json] content is not a valid json. It must contain json data `{'image':'DOCKER-IMAGE-NAME', 'tag':'TAG'}` format.");

        artifactPlanConfig.imageToPush(agentWorkingDir.getAbsolutePath(), environmentVariables);
    }

    @Test
    public void shouldErrorOutWhenFileDoesNotExist() {
        final ArtifactPlanConfig artifactPlanConfig = new BuildFileArtifactPlanConfig("random.json");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(String.format("%s/random.json (No such file or directory)", agentWorkingDir.getAbsolutePath()));

        artifactPlanConfig.imageToPush(agentWorkingDir.getAbsolutePath(), environmentVariables);
    }
}
