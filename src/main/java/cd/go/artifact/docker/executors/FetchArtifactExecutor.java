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
import cd.go.artifact.docker.DockerPullEventListener;
import cd.go.artifact.docker.DockerPullResponse;
import cd.go.artifact.docker.model.ArtifactStoreConfig;
import cd.go.artifact.docker.model.FetchArtifactConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.OutputHandle;

import java.util.Map;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static cd.go.artifact.docker.utils.Util.GSON;
import static java.lang.String.format;

public class FetchArtifactExecutor implements RequestExecutor {
    private FetchArtifactRequest fetchArtifactRequest;
    private DockerClientFactory clientFactory;

    public FetchArtifactExecutor(GoPluginApiRequest request) {
        this(request, DockerClientFactory.instance());
    }

    FetchArtifactExecutor(GoPluginApiRequest request, DockerClientFactory clientFactory) {
        this.fetchArtifactRequest = FetchArtifactRequest.fromJSON(request.requestBody());
        this.clientFactory = clientFactory;
    }

    @Override
    public GoPluginApiResponse execute() {
        try {
            DockerPullResponse dockerPullResponse = fetch();
            if (dockerPullResponse.hasException()) {
                throw dockerPullResponse.getThrowable();
            }
            return DefaultGoPluginApiResponse.success("");

        } catch (Throwable e) {
            final String message = String.format("Failed pull docker image %s: %s", fetchArtifactRequest.getMetadata().get("docker-image"), e);
            LOG.error(message);
            return DefaultGoPluginApiResponse.error(message);
        }
    }

    private DockerPullResponse fetch() throws Exception {
        final String imageToPull = (String) fetchArtifactRequest.getMetadata().get("docker-image");
        LOG.info(format("Pulling docker image `%s` to docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));

        DockerClient docker = clientFactory.docker(fetchArtifactRequest.getArtifactStoreConfig());
        final DockerPullEventListener dockerPullEventListener = new DockerPullEventListener();
        OutputHandle handle = docker.image().withName(imageToPull).pull()
                .usingListener(dockerPullEventListener)
                .fromRegistry();

        final DockerPullResponse dockerPullResponse = dockerPullEventListener.await();
        handle.close();
        docker.close();
        return dockerPullResponse;
    }

    protected static class FetchArtifactRequest {
        @Expose
        @SerializedName("store_configuration")
        private ArtifactStoreConfig artifactStoreConfig;
        @Expose
        @SerializedName("fetch_artifact_configuration")
        private FetchArtifactConfig fetchArtifactConfig;
        @Expose
        @SerializedName("artifact_metadata")
        private Map<String, Object> metadata;

        public FetchArtifactRequest() {
        }

        public FetchArtifactRequest(ArtifactStoreConfig artifactStoreConfig, FetchArtifactConfig fetchArtifactConfig, Map<String, Object> metadata) {
            this.artifactStoreConfig = artifactStoreConfig;
            this.fetchArtifactConfig = fetchArtifactConfig;
            this.metadata = metadata;
        }

        public ArtifactStoreConfig getArtifactStoreConfig() {
            return artifactStoreConfig;
        }

        public FetchArtifactConfig getFetchArtifactConfig() {
            return fetchArtifactConfig;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public static FetchArtifactRequest fromJSON(String json) {
            return GSON.fromJson(json, FetchArtifactRequest.class);
        }
    }
}
