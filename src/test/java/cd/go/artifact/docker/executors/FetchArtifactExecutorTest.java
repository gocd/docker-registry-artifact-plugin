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
import cd.go.artifact.docker.model.ArtifactStoreConfig;
import cd.go.artifact.docker.model.FetchArtifactConfig;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.fabric8.docker.api.model.AuthConfig;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FetchArtifactExecutorTest extends DockerMockServerTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private GoPluginApiRequest request;
    @Mock
    private DockerClientFactory dockerClientFactory;

    @Before
    public void setUp() {
        initMocks(this);

        when(dockerClientFactory.docker(any())).thenReturn(getClient());
    }

    @Test
    public void shouldFetchArtifact() throws IOException, InterruptedException {
        final FetchArtifactExecutor.FetchArtifactRequest fetchArtifactRequest = new FetchArtifactExecutor.FetchArtifactRequest(new ArtifactStoreConfig(), new FetchArtifactConfig(), Collections.singletonMap("docker-image", "localhost:5000/alpine:v1"));

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));
        expect().post()
                .withPath("/images/create?fromImage=localhost:5000/alpine:v1")
                .andReturn(200, "")
                .always();

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, dockerClientFactory).execute();

        final RecordedRequest recordedRequest = takeRequest();

        final AuthConfig auth = new Gson().fromJson(new String(Base64.getDecoder().decode(recordedRequest.getHeader("X-Registry-Auth"))), AuthConfig.class);
        assertThat(auth).isEqualTo(new AuthConfig("", null, "admin", "localhost:5000", "admin"));
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("");
    }

    @Test
    public void shouldAddErrorToPublishArtifactResponseWhenFailedToPublishImage() {
        final FetchArtifactExecutor.FetchArtifactRequest fetchArtifactRequest = new FetchArtifactExecutor.FetchArtifactRequest(new ArtifactStoreConfig(), new FetchArtifactConfig(), Collections.singletonMap("docker-image", "localhost:5000/alpine:v1"));

        when(request.requestBody()).thenReturn(new Gson().toJson(fetchArtifactRequest));

        expect().post()
                .withPath("/images/create?fromImage=localhost:5000/alpine:v1")
                .andReturn(400, "Some error")
                .always();

        final GoPluginApiResponse response = new FetchArtifactExecutor(request, dockerClientFactory).execute();

        assertThat(response.responseCode()).isEqualTo(500);
        assertThat(response.responseBody()).isEqualTo("Failed pull docker image localhost:5000/alpine:v1: java.io.IOException: Some error");
    }
}