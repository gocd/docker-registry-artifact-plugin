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

package cd.go.artifact.docker.executors;

import cd.go.artifact.docker.DockerClientFactory;
import cd.go.artifact.docker.DockerProgressHandler;
import cd.go.artifact.docker.model.ArtifactInfo;
import cd.go.artifact.docker.model.ArtifactPlan;
import cd.go.artifact.docker.model.ArtifactStoreConfig;
import cd.go.artifact.docker.model.PublishArtifactConfig;
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
import org.mockito.ArgumentCaptor;
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
    private DockerClientFactory dockerClientFactory;
    @Mock
    private DefaultDockerClient dockerClient;

    private File agentWorkingDir;

    @Before
    public void setUp() throws IOException, InterruptedException, DockerException, DockerCertificateException {
        initMocks(this);
        agentWorkingDir = tmpFolder.newFolder("go-agent");

        when(dockerClientFactory.docker(any())).thenReturn(dockerClient);
    }

    @Test
    public void shouldPublishArtifact() throws IOException, DockerException, InterruptedException {
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "admin", "admin123");
        final PublishArtifactConfig publishArtifactConfig = publishArtifactConfig(agentWorkingDir.getAbsolutePath(), storeConfig, new ArtifactPlan("id", "storeId", "build.json"));
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactConfig.toJSON());

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, dockerClientFactory).execute();

        verify(dockerClient).push(eq("localhost:5000/alpine:3.6"), any(ProgressHandler.class));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"id\":{\"image\":\"localhost:5000/alpine:3.6\"}},\"errors\":[]}");
    }

    @Test
    public void shouldAddErrorToPublishArtifactResponseWhenFailedToPublishImage() throws IOException, DockerException, InterruptedException {
        final ArtifactStoreConfig artifactStoreConfig = new ArtifactStoreConfig("localhost:5000", "admin", "admin123");
        final PublishArtifactConfig publishArtifactConfig = publishArtifactConfig(agentWorkingDir.getAbsolutePath(), artifactStoreConfig, new ArtifactPlan("id", "storeId", "build.json"));
        final ArgumentCaptor<DockerProgressHandler> argumentCaptor = ArgumentCaptor.forClass(DockerProgressHandler.class);
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactConfig.toJSON());
        doThrow(new RuntimeException("Some error")).when(dockerClient).push(eq("localhost:5000/alpine:3.6"), argumentCaptor.capture());

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{},\"errors\":[\"Failed to publish Artifact[id\\u003did, storeId\\u003dstoreId, buildFile\\u003dbuild.json]: java.lang.RuntimeException: Some error\"]}");
    }

    private PublishArtifactConfig publishArtifactConfig(String agentDir, ArtifactStoreConfig artifactStoreConfig, ArtifactPlan... artifactPlans) {
        return new PublishArtifactConfig(agentDir,
                new ArtifactInfo("storeId", artifactStoreConfig, artifactPlans)
        );
    }
}