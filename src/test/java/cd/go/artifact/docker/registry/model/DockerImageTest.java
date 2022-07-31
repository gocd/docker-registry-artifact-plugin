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

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.MockitoAnnotations.openMocks;

public class DockerImageTest {
    @TempDir
    public Path tmpFolder;

    private Path agentWorkingDir;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
        agentWorkingDir = Files.createDirectory(tmpFolder.resolve("go-agent"));
    }

    @Test
    public void shouldDeserializeFileToDockerImage() throws IOException {
        Path path = Paths.get(agentWorkingDir.toString(), "build-file.json");
        Files.write(path, "{\"image\":\"alpine\",\"tag\":\"3.6\"}".getBytes());

        final DockerImage dockerImage = DockerImage.fromFile(path.toFile());

        assertThat(dockerImage.getImage()).isEqualTo("alpine");
        assertThat(dockerImage.getTag()).isEqualTo("3.6");
    }

    @Test
    public void shouldErrorOutWhenFileContentIsNotAValidJSON() throws IOException {
        Path path = Paths.get(agentWorkingDir.toString(), "build-file.json");
        Files.write(path, "bar".getBytes());

        assertThatThrownBy(() -> DockerImage.fromFile(path.toFile()))
                .isInstanceOf(JsonSyntaxException.class)
                .hasMessageContaining("Expected BEGIN_OBJECT but was STRING at line 1 column 1 path");
    }

    @Test
    public void shouldErrorOutWhenFileContentIsJSONArray() throws IOException {
        Path path = Paths.get(agentWorkingDir.toString(), "build-file.json");
        Files.write(path, "[{}]".getBytes());

        assertThatThrownBy(() -> DockerImage.fromFile(path.toFile()))
                .isInstanceOf(JsonSyntaxException.class)
                .hasMessageContaining("Expected BEGIN_OBJECT but was BEGIN_ARRAY at line 1 column 2 path");
    }

    @Test
    public void shouldErrorOutWhenFileDoesNotExist() throws IOException {
        assertThatThrownBy(() -> DockerImage.fromFile(agentWorkingDir.resolve("random.json").toFile()))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessageContaining(String.format("%s/random.json (No such file or directory)", agentWorkingDir.toString()));
    }
}