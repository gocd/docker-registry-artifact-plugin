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

import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class DockerImageTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File agentWorkingDir;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        agentWorkingDir = tmpFolder.newFolder("go-agent");
    }

    @Test
    public void shouldDeserializeFileToDockerImage() throws IOException {
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build-file.json");
        Files.write(path, "{\"image\":\"alpine\",\"tag\":\"3.6\"}".getBytes());

        final DockerImage dockerImage = DockerImage.fromFile(path.toFile());

        assertThat(dockerImage.getImage()).isEqualTo("alpine");
        assertThat(dockerImage.getTag()).isEqualTo("3.6");
    }

    @Test
    public void shouldErrorOutWhenFileContentIsNotAValidJSON() throws IOException {
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build-file.json");
        Files.write(path, "bar".getBytes());


        thrown.expect(JsonSyntaxException.class);
        thrown.expectMessage("Expected BEGIN_OBJECT but was STRING at line 1 column 1 path");

        DockerImage.fromFile(path.toFile());
    }

    @Test
    public void shouldErrorOutWhenFileContentIsJSONArray() throws IOException {
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build-file.json");
        Files.write(path, "[{}]".getBytes());

        thrown.expect(JsonSyntaxException.class);
        thrown.expectMessage("Expected BEGIN_OBJECT but was BEGIN_ARRAY at line 1 column 2 path");

        DockerImage.fromFile(path.toFile());
    }

    @Test
    public void shouldErrorOutWhenFileDoesNotExist() throws IOException {
        thrown.expect(FileNotFoundException.class);
        if (SystemUtils.IS_OS_WINDOWS) {
            thrown.expectMessage(String.format("%s%srandom.json (The system cannot find the file specified)", agentWorkingDir.getAbsolutePath(), File.separator));
        }
        else {
            thrown.expectMessage(String.format("%s&srandom.json (No such file or directory)", agentWorkingDir.getAbsolutePath(), File.separator));
        }

        DockerImage.fromFile(new File(agentWorkingDir,"random.json"));
    }
}