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

package cd.go.artifact.docker.registry.executors;

import cd.go.artifact.docker.registry.ConsoleLogger;
import cd.go.artifact.docker.registry.DockerClientFactory;
import cd.go.artifact.docker.registry.DockerProgressHandler;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(SystemStubsExtension.class)
public class PublishArtifactExecutorTest {
    @TempDir
    public Path tmpFolder;

    @Mock
    private GoPluginApiRequest request;
    @Mock
    private ConsoleLogger consoleLogger;
    @Mock
    private DockerProgressHandler dockerProgressHandler;
    @Mock
    private DefaultDockerClient dockerClient;
    @Mock
    private DockerClientFactory dockerClientFactory;

    @SystemStub
    public EnvironmentVariables environmentVariables;

    private Path agentWorkingDir;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException, DockerException, DockerCertificateException {
        openMocks(this);
        agentWorkingDir = Files.createDirectory(tmpFolder.resolve("go-agent"));

        when(dockerClientFactory.docker(any())).thenReturn(dockerClient);
    }

    @Test
    public void shouldPublishArtifactUsingBuildFile() throws IOException, DockerException, InterruptedException {
        final ArtifactPlan artifactPlan = new ArtifactPlan("id", "storeId", "build.json");
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final ArtifactStore artifactStore = new ArtifactStore(artifactPlan.getId(), storeConfig);
        final PublishArtifactRequest publishArtifactRequest = new PublishArtifactRequest(artifactStore, artifactPlan, agentWorkingDir.toString());

        Path path = agentWorkingDir.resolve("build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactRequest.toJSON());
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        verify(dockerClient).push(eq("localhost:5000/alpine:3.6"), any(ProgressHandler.class));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"image\":\"localhost:5000/alpine:3.6\",\"digest\":\"foo\"}}");
    }

    @Test
    public void shouldPublishArtifactUsingImageAndTag() throws DockerException, InterruptedException {
        final ArtifactPlan artifactPlan = new ArtifactPlan("id", "storeId", "alpine", Optional.of("3.6"));
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final ArtifactStore artifactStore = new ArtifactStore(artifactPlan.getId(), storeConfig);
        final PublishArtifactRequest publishArtifactRequest = new PublishArtifactRequest(artifactStore, artifactPlan, agentWorkingDir.toString());

        when(request.requestBody()).thenReturn(publishArtifactRequest.toJSON());
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        verify(dockerClient).push(eq("alpine:3.6"), any(ProgressHandler.class));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"image\":\"alpine:3.6\",\"digest\":\"foo\"}}");
    }

    @Test
    public void shouldAddErrorToPublishArtifactResponseWhenFailedToPublishImage() throws IOException, DockerException, InterruptedException {
        final ArtifactPlan artifactPlan = new ArtifactPlan("id", "storeId", "build.json");
        final ArtifactStoreConfig artifactStoreConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final ArtifactStore artifactStore = new ArtifactStore(artifactPlan.getId(), artifactStoreConfig);
        final PublishArtifactRequest publishArtifactRequest = new PublishArtifactRequest(artifactStore, artifactPlan, agentWorkingDir.toString());
        final ArgumentCaptor<DockerProgressHandler> argumentCaptor = ArgumentCaptor.forClass(DockerProgressHandler.class);

        Path path = Paths.get(agentWorkingDir.toString(), "build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactRequest.toJSON());
        doThrow(new RuntimeException("Some error")).when(dockerClient).push(eq("localhost:5000/alpine:3.6"), argumentCaptor.capture());

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(500);
        assertThat(response.responseBody()).contains("Failed to publish Artifact[id=id, storeId=storeId, artifactPlanConfig={\"BuildFile\":\"build.json\"}]: Some error");
    }

    @Test
    public void shouldReadEnvironmentVariablesPassedFromServer() throws IOException, DockerException, InterruptedException {
        final ArtifactPlan artifactPlan = new ArtifactPlan("id", "storeId", "${IMAGE_NAME}", Optional.of("3.6"));
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final ArtifactStore artifactStore = new ArtifactStore(artifactPlan.getId(), storeConfig);
        final PublishArtifactRequest publishArtifactRequest = new PublishArtifactRequest(artifactStore, artifactPlan, agentWorkingDir.toString(), Collections.singletonMap("IMAGE_NAME", "alpine"));

        when(request.requestBody()).thenReturn(publishArtifactRequest.toJSON());
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        verify(dockerClient).push(eq("alpine:3.6"), any(ProgressHandler.class));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"image\":\"alpine:3.6\",\"digest\":\"foo\"}}");
    }

    @Test
    public void shouldReadEnvironmentVariablesFromTheSystem() throws IOException, DockerException, InterruptedException {
        environmentVariables.set("IMAGE_NAME", "alpine");
        final ArtifactPlan artifactPlan = new ArtifactPlan("id", "storeId", "${IMAGE_NAME}", Optional.of("3.6"));
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final ArtifactStore artifactStore = new ArtifactStore(artifactPlan.getId(), storeConfig);
        final PublishArtifactRequest publishArtifactRequest = new PublishArtifactRequest(artifactStore, artifactPlan, agentWorkingDir.toString());

        when(request.requestBody()).thenReturn(publishArtifactRequest.toJSON());
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        verify(dockerClient).push(eq("alpine:3.6"), any(ProgressHandler.class));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"image\":\"alpine:3.6\",\"digest\":\"foo\"}}");
    }
}
