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
import cd.go.artifact.docker.registry.model.*;
import com.spotify.docker.client.DockerClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

import static cd.go.artifact.docker.registry.DockerRegistryArtifactPlugin.LOG;
import static java.lang.String.format;

public class PublishArtifactExecutor implements RequestExecutor {
    private final PublishArtifactRequest publishArtifactRequest;
    private final PublishArtifactResponse publishArtifactResponse;
    private final ConsoleLogger consoleLogger;
    private final DockerProgressHandler progressHandler;
    private final DockerClientFactory clientFactory;

    public PublishArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger) {
        this(request, consoleLogger, new DockerProgressHandler(consoleLogger), DockerClientFactory.instance());
    }

    PublishArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger, DockerProgressHandler progressHandler, DockerClientFactory clientFactory) {
        this.publishArtifactRequest = PublishArtifactRequest.fromJSON(request.requestBody());
        this.consoleLogger = consoleLogger;
        this.progressHandler = progressHandler;
        this.clientFactory = clientFactory;
        publishArtifactResponse = new PublishArtifactResponse();
    }

    @Override
    public GoPluginApiResponse execute() {
        ArtifactPlan artifactPlan = publishArtifactRequest.getArtifactPlan();
        final ArtifactStoreConfig artifactStoreConfig = publishArtifactRequest.getArtifactStore().getArtifactStoreConfig();
        try {
            final DockerClient docker = clientFactory.docker(artifactStoreConfig);
            Map<String, String> environmentVariables = publishArtifactRequest.getEnvironmentVariables() == null ? new HashMap<>() : publishArtifactRequest.getEnvironmentVariables();
            environmentVariables.putAll(System.getenv());
            final DockerImage image = artifactPlan.getArtifactPlanConfig().imageToPush(publishArtifactRequest.getAgentWorkingDir(), environmentVariables);

            LOG.info(format("Pushing docker image `%s` to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));
            consoleLogger.info(format("Pushing docker image `%s` to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));

            docker.push(image.toString(), progressHandler);
            docker.close();

            publishArtifactResponse.addMetadata("image", image.toString());
            publishArtifactResponse.addMetadata("digest", progressHandler.getDigest());
            consoleLogger.info(format("Image `%s` successfully pushed to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));
            return DefaultGoPluginApiResponse.success(publishArtifactResponse.toJSON());
        } catch (Exception e) {
            consoleLogger.error(String.format("Failed to publish %s: %s", artifactPlan, e));
            LOG.error(String.format("Failed to publish %s: %s", artifactPlan, e.getMessage()), e);
            return DefaultGoPluginApiResponse.error(String.format("Failed to publish %s: %s", artifactPlan, e.getMessage()));
        }
    }
}
