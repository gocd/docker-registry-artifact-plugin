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
import cd.go.artifact.docker.registry.DockerProgressHandler;
import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import cd.go.artifact.docker.registry.model.FetchArtifactConfig;
import com.google.gson.Gson;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static cd.go.artifact.docker.registry.executors.FetchArtifactExecutor.FetchArtifactRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class FetchArtifactExecutorTest {
    @Mock
    private GoPluginApiRequest request;
    @Mock
    private DockerClientFactory dockerClientFactory;
    @Mock
    private DefaultDockerClient dockerClient;
    @Mock
    private DockerProgressHandler dockerProgressHandler;
    @Mock
    private ConsoleLogger consoleLogger;

    @BeforeEach
    public void setUp() throws InterruptedException, DockerException, DockerCertificateException {
        openMocks(this);

        when(dockerClientFactory.docker(any())).thenReturn(dockerClient);
    }

    @Test
    public void shouldFetchArtifact() {
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final HashMap<String, String> artifactMetadata = new HashMap<>();
        artifactMetadata.put("image", "localhost:5000/alpine:v1");
        artifactMetadata.put("digest", "foo");

        FetchArtifactConfig fetchArtifactConfig = new FetchArtifactConfig("PREFIX", null);

        final FetchArtifactRequest fetchArtifactRequest = new FetchArtifactRequest(storeConfig, artifactMetadata, fetchArtifactConfig);

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("[{\"name\":\"PREFIX_ARTIFACT_IMAGE\",\"value\":\"localhost:5000/alpine:v1\"}]");
    }

    @Test
    public void shouldNotFetchArtifactWhenSkipImagePullingIsToggled() throws Exception {
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final HashMap<String, String> artifactMetadata = new HashMap<>();
        artifactMetadata.put("image", "localhost:5000/alpine:v1");
        artifactMetadata.put("digest", "foo");

        FetchArtifactConfig fetchArtifactConfig = new FetchArtifactConfig("", "true");

        final FetchArtifactRequest fetchArtifactRequest = new FetchArtifactRequest(storeConfig, artifactMetadata, fetchArtifactConfig);

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        verify(dockerClient, times(0)).pull("localhost:5000/alpine:v1", dockerProgressHandler);

        assertThat(response.responseCode()).isEqualTo(200);
    }

    @Test
    public void shouldSetEnvironmentVariablesWithImageInformationInResponseRegardlessOfWhetherThePrefixIsProvided() {
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final HashMap<String, String> artifactMetadata = new HashMap<>();
        artifactMetadata.put("image", "localhost:5000/alpine:v1");
        artifactMetadata.put("digest", "foo");
        final FetchArtifactRequest fetchArtifactRequest = new FetchArtifactRequest(storeConfig, artifactMetadata, new FetchArtifactConfig());

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));
        when(dockerProgressHandler.getDigest()).thenReturn("foo");

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("[{\"name\":\"ARTIFACT_IMAGE\",\"value\":\"localhost:5000/alpine:v1\"}]");
    }


    @Test
    public void shouldErrorOutWhenDigestIsNotSame() {
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final HashMap<String, String> artifactMetadata = new HashMap<>();
        artifactMetadata.put("image", "localhost:5000/alpine:v1");
        artifactMetadata.put("digest", "foo");
        final FetchArtifactRequest fetchArtifactRequest = new FetchArtifactRequest(storeConfig, artifactMetadata, new FetchArtifactConfig());

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));
        when(dockerProgressHandler.getDigest()).thenReturn("bar");

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(500);
        assertThat(response.responseBody()).isEqualTo("Failed pull docker image: java.lang.RuntimeException: Expecting pulled image digest to be [foo] but it is [bar].");
    }

    @Test
    public void shouldErrorOutWhenFailedToPull() throws DockerException, InterruptedException {
        final ArtifactStoreConfig storeConfig = new ArtifactStoreConfig("localhost:5000", "other", "admin", "admin123");
        final HashMap<String, String> artifactMetadata = new HashMap<>();
        artifactMetadata.put("image", "localhost:5000/alpine:v1");
        artifactMetadata.put("digest", "foo");
        final FetchArtifactRequest fetchArtifactRequest = new FetchArtifactRequest(storeConfig, artifactMetadata, new FetchArtifactConfig());

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));
        doThrow(new RuntimeException("Some error")).when(dockerClient).pull("localhost:5000/alpine:v1", dockerProgressHandler);

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, consoleLogger, dockerProgressHandler, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(500);
        assertThat(response.responseBody()).isEqualTo("Failed pull docker image: java.lang.RuntimeException: Some error");
    }
}