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
import cd.go.artifact.docker.DockerMockServerTestBase;
import cd.go.artifact.docker.model.ArtifactInfo;
import cd.go.artifact.docker.model.ArtifactPlan;
import cd.go.artifact.docker.model.ArtifactStoreConfig;
import cd.go.artifact.docker.model.PublishArtifactConfig;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.fabric8.docker.api.model.AuthConfig;
import okhttp3.mockwebserver.RecordedRequest;
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
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PublishArtifactExecutorTest extends DockerMockServerTestBase {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private GoPluginApiRequest request;
    @Mock
    private DockerClientFactory dockerClientFactory;

    private File agentWorkingDir;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
        agentWorkingDir = tmpFolder.newFolder("go-agent");

        when(dockerClientFactory.docker(any())).thenReturn(getClient());
    }

    @Test
    public void shouldPublishArtifact() throws IOException, InterruptedException {
        final PublishArtifactConfig publishArtifactConfig = publishArtifactConfig(agentWorkingDir.getAbsolutePath(), new ArtifactStoreConfig(), new ArtifactPlan("id", "storeId", "build.json"));
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactConfig.toJSON());
        expect().post()
                .withPath("/images/localhost:5000/alpine/push?force=true&tag=3.6")
                .andReturn(200, "")
                .always();

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, dockerClientFactory).execute();

        final RecordedRequest recordedRequest = takeRequest();

        final AuthConfig auth = new Gson().fromJson(new String(Base64.getDecoder().decode(recordedRequest.getHeader("X-Registry-Auth"))), AuthConfig.class);
        assertThat(auth).isEqualTo(new AuthConfig("",null,"admin","localhost:5000","admin"));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{\"docker-image\":\"localhost:5000/alpine:3.6\"},\"errors\":[]}");
    }

    @Test
    public void shouldAddErrorToPublishArtifactResponseWhenFailedToPublishImage() throws IOException, InterruptedException {
        final PublishArtifactConfig publishArtifactConfig = publishArtifactConfig(agentWorkingDir.getAbsolutePath(), new ArtifactStoreConfig(), new ArtifactPlan("id", "storeId", "build.json"));
        Path path = Paths.get(agentWorkingDir.getAbsolutePath(), "build.json");
        Files.write(path, "{\"image\":\"localhost:5000/alpine\",\"tag\":\"3.6\"}".getBytes());

        when(request.requestBody()).thenReturn(publishArtifactConfig.toJSON());
        expect().post()
                .withPath("/images/localhost:5000/alpine/push?force=true&tag=3.6")
                .andReturn(400, "Some error")
                .always();

        final GoPluginApiResponse response = new PublishArtifactExecutor(request, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"metadata\":{},\"errors\":[\"Failure: java.io.IOException: Some error\"]}");
    }

    private PublishArtifactConfig publishArtifactConfig(String agentDir, ArtifactStoreConfig artifactStoreConfig, ArtifactPlan... artifactPlans) {
        return new PublishArtifactConfig(agentDir,
                new ArtifactInfo("storeId", artifactStoreConfig, artifactPlans)
        );
    }
}