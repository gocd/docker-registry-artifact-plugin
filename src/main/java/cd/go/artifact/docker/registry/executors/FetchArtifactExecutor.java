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
import cd.go.artifact.docker.registry.utils.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spotify.docker.client.DockerClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static cd.go.artifact.docker.registry.DockerRegistryArtifactPlugin.LOG;
import static java.lang.String.format;

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
        try {
            final Map<String, String> artifactMap = fetchArtifactRequest.getMetadata();
            validateMetadata(artifactMap);
            FetchArtifactConfig fetchArtifactConfig = fetchArtifactRequest.getFetchArtifactConfig();

            final String artifactPrefix = fetchArtifactConfig.getEnvironmentVariablePrefix();
            final String imageToPull = artifactMap.get("image");

            consoleLogger.info(String.format("Pulling docker image `%s` from docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));
            LOG.info(String.format("Pulling docker image `%s` from docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));

            DockerClient docker = clientFactory.docker(fetchArtifactRequest.getArtifactStoreConfig());
            docker.pull(imageToPull, dockerProgressHandler);
            docker.close();

            consoleLogger.info(String.format("Image `%s` successfully pulled from docker registry `%s`.", imageToPull, fetchArtifactRequest.getArtifactStoreConfig().getRegistryUrl()));

            if (!dockerProgressHandler.getDigest().equals(artifactMap.get("digest"))) {
                throw new RuntimeException(format("Expecting pulled image digest to be [%s] but it is [%s].", artifactMap.get("digest"), dockerProgressHandler.getDigest()));
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", StringUtils.isEmpty(artifactPrefix) ? "ARTIFACT_IMAGE" : String.format("%s_ARTIFACT_IMAGE", artifactPrefix));
            jsonObject.addProperty("value", imageToPull);

            JsonArray jsonElements = new JsonArray();
            jsonElements.add(jsonObject);

            return DefaultGoPluginApiResponse.success(jsonElements.toString());
        } catch (Exception e) {
            final String message = format("Failed pull docker image: %s", e);
            consoleLogger.error(message);
            LOG.error(message);
            return DefaultGoPluginApiResponse.error(message);
        }
    }

    public void validateMetadata(Map<String, String> artifactMap) {
        if (artifactMap == null) {
            throw new RuntimeException(format("Cannot fetch the docker image from registry: Invalid metadata received from the GoCD server. The artifact metadata is null."));
        }

        if (!artifactMap.containsKey("image")) {
            throw new RuntimeException(format("Cannot fetch the docker image from registry: Invalid metadata received from the GoCD server. The artifact metadata must contain the key `%s`.", "image"));
        }

        if (!artifactMap.containsKey("digest")) {
            throw new RuntimeException(format("Cannot fetch the docker image from registry: Invalid metadata received from the GoCD server. The artifact metadata must contain the key `%s`.", "digest"));
        }
    }

    protected static class FetchArtifactRequest {

        @Expose
        @SerializedName("fetch_artifact_configuration")
        private FetchArtifactConfig fetchArtifactConfig;

        @Expose
        @SerializedName("store_configuration")
        private ArtifactStoreConfig artifactStoreConfig;

        @Expose
        @SerializedName("artifact_metadata")
        private Map<String, String> metadata;

        public FetchArtifactRequest() {
        }

        public FetchArtifactRequest(ArtifactStoreConfig artifactStoreConfig, Map<String, String> metadata, FetchArtifactConfig fetchArtifactConfig) {
            this.artifactStoreConfig = artifactStoreConfig;
            this.metadata = metadata;
            this.fetchArtifactConfig = fetchArtifactConfig;
        }

        public ArtifactStoreConfig getArtifactStoreConfig() {
            return artifactStoreConfig;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public static FetchArtifactRequest fromJSON(String json) {
            return Util.GSON.fromJson(json, FetchArtifactRequest.class);
        }

        public FetchArtifactConfig getFetchArtifactConfig() {
            return fetchArtifactConfig;
        }
    }
}
