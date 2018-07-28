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

package cd.go.artifact.docker.registry.executors;

import cd.go.artifact.docker.registry.ConsoleLogger;
import cd.go.artifact.docker.registry.DockerClientFactory;
import cd.go.artifact.docker.registry.model.ArtifactPlan;
import cd.go.artifact.docker.registry.model.ArtifactStore;
import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import cd.go.artifact.docker.registry.model.PublishArtifactRequest;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PublishArtifactExecutorTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private GoPluginApiRequest request;
    @Mock
    private ConsoleLogger consoleLogger;
    @Mock
    private DefaultDockerClient dockerClient;
    @Mock
    private DockerClientFactory dockerClientFactory;

    private File agentWorkingDir;

    @Before
    public void setUp() throws IOException, InterruptedException, DockerException, DockerCertificateException {
        initMocks(this);
        agentWorkingDir = tmpFolder.newFolder("go-agent");

        when(dockerClientFactory.docker(any())).thenReturn(dockerClient);
    }

    @Test
    public void shouldPublishArtifactUsingSourceFile() throws IOException, DockerException, InterruptedException {
        final ArtifactPlan artifactPlan = new ArtifactPlan("id", "storeId", "build.json");
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "admin", "admin123");
        final ArtifactStore artifactStore = new ArtifactStore(artifactPlan.getId(), storeConfig);
        final PublishArtifactRequest publishArtifactRequest = new PublishArtifactRequest(artifactStore, artifactPlan, agentWorkingDir.getAbsolutePath());

        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactRequest.toJSON());

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, consoleLogger, dockerClientFactory).execute();

        verify(dockerClient).push(eq("localhost:5000/alpine:3.6"));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"image\":\"localhost:5000/alpine:3.6\"}}");
    }
}
