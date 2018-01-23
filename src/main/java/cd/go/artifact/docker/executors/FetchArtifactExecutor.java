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

import cd.go.artifact.docker.ConsoleLogger;
import cd.go.artifact.docker.DockerClientFactory;
import cd.go.artifact.docker.DockerProgressHandler;
import cd.go.artifact.docker.model.ArtifactStoreConfig;
import cd.go.artifact.docker.model.FetchArtifact;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spotify.docker.client.DockerClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Map;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static cd.go.artifact.docker.utils.Util.GSON;
import static java.lang.String.format;
import static java.lang.String.join;

public class FetchArtifactExecutor implements RequestExecutor {
    private FetchArtifactRequest fetchArtifactRequest;
    private final ConsoleLogger consoleLogger;
    private DockerClientFactory clientFactory;
    private final DockerProgressHandler dockerProgressHandler;

    public FetchArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger) {
        this(request, consoleLogger, new DockerProgressHandler(consoleLogger), DockerClientFactory.instance());
    }

    FetchArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger, DockerProgressHandler dockerProgressHandler, DockerClientFactory clientFactory) {
        this.fetchArtifactRequest = FetchArtifactRequest.fromJSON(request.requestBody());
        this.consoleLogger = consoleLogger;
        this.clientFactory = clientFactory;
        this.dockerProgressHandler = dockerProgressHandler;
    }

    @Override
    public GoPluginApiResponse execute() {
        final String artifactId = fetchArtifactRequest.getFetchArtifact().getArtifactId();
        try {
            final Map<String, String> artifactMap = (Map<String, String>) fetchArtifactRequest.getMetadata().get(artifactId);
            if (artifactMap == null) {
                throw new RuntimeException(format("Invalid metadata received from server. It must contain key `%s`.", artifactId));
            }

            fetch(artifactMap.get("image"));

            if (!dockerProgressHandler.getErrors().isEmpty()) {
                throw new RuntimeException(join("\n", dockerProgressHandler.getErrors()));
            }

            if (!dockerProgressHandler.getDigest().equals(artifactMap.get("digest"))) {
                throw new RuntimeException(format("Expecting pulled image digest to be [%s] but it is [%s].", artifactMap.get("digest"), dockerProgressHandler.getDigest()));
            }

            return DefaultGoPluginApiResponse.success("");
        } catch (Exception e) {
            final String message = format("Failed pull docker image: %s", e);
            consoleLogger.error(message);
            LOG.error(message);
            return DefaultGoPluginApiResponse.error(message);
        }
    }

    private void fetch(String imageToPull) throws Exception {
        consoleLogger.info(format("Pulling docker image `%s` to docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));
        LOG.info(format("Pulling docker image `%s` to docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));

        DockerClient docker = clientFactory.docker(fetchArtifactRequest.getArtifactStoreConfig());
        docker.pull(imageToPull, dockerProgressHandler);
        docker.close();

        consoleLogger.info(format("Image `%s` successfully pulled from docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));
    }

    protected static class FetchArtifactRequest {
        @Expose
        @SerializedName("store_configuration")
        private ArtifactStoreConfig artifactStoreConfig;
        @Expose
        @SerializedName("fetch_artifact")
        private FetchArtifact fetchArtifact;
        @Expose
        @SerializedName("artifact_metadata")
        private Map<String, Object> metadata;

        public FetchArtifactRequest() {
        }

        public FetchArtifactRequest(ArtifactStoreConfig artifactStoreConfig, String artifactId, Map<String, Object> metadata) {
            this.artifactStoreConfig = artifactStoreConfig;
            this.fetchArtifact = new FetchArtifact(artifactId);
            this.metadata = metadata;
        }

        public ArtifactStoreConfig getArtifactStoreConfig() {
            return artifactStoreConfig;
        }

        public FetchArtifact getFetchArtifact() {
            return fetchArtifact;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public static FetchArtifactRequest fromJSON(String json) {
            return GSON.fromJson(json, FetchArtifactRequest.class);
        }
    }
}
