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
import cd.go.artifact.docker.DockerEventListener;
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
            fetch();
        } catch (Exception e) {
            LOG.info(String.format("Failed to download artifact %s - %s", fetchArtifactRequest.getMetadata().get("docker-image"), e));
            return DefaultGoPluginApiResponse.error(e.getMessage());
        }
        return DefaultGoPluginApiResponse.success("");
    }

    private void fetch() throws Exception {
        LOG.info(String.valueOf(fetchArtifactRequest.getMetadata()));
        LOG.info(String.valueOf(fetchArtifactRequest.getArtifactStoreConfig()));
        LOG.info(String.format("Fetching artifact %s", fetchArtifactRequest.getFetchArtifactConfig()));

        DockerClient docker = clientFactory.docker(fetchArtifactRequest.getArtifactStoreConfig());
        final DockerEventListener dockerEventListener = new DockerEventListener();
        OutputHandle handle = docker.image().withName((String) fetchArtifactRequest.getMetadata().get("docker-image")).pull()
                .usingListener(dockerEventListener)
                .fromRegistry();

        dockerEventListener.await();
        handle.close();
        docker.close();
    }

    private static class FetchArtifactRequest {
        @Expose
        @SerializedName("store_configuration")
        private ArtifactStoreConfig artifactStoreConfig;
        @Expose
        @SerializedName("fetch_artifact_configuration")
        private FetchArtifactConfig fetchArtifactConfig;
        @Expose
        @SerializedName("artifact_metadata")
        private Map<String, Object> metadata;

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
