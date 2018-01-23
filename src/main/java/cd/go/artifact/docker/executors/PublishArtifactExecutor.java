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
import cd.go.artifact.docker.model.*;
import com.spotify.docker.client.DockerClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static java.lang.String.format;

public class PublishArtifactExecutor implements RequestExecutor {
    private final PublishArtifactRequest publishArtifactRequest;
    private final PublishArtifactResponse publishArtifactResponse;
    private final ConsoleLogger consoleLogger;
    private final DockerClientFactory clientFactory;

    public PublishArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger) {
        this(request, consoleLogger, DockerClientFactory.instance());
    }

    PublishArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger, DockerClientFactory clientFactory) {
        this.publishArtifactRequest = PublishArtifactRequest.fromJSON(request.requestBody());
        this.consoleLogger = consoleLogger;
        this.clientFactory = clientFactory;
        publishArtifactResponse = new PublishArtifactResponse();
    }

    @Override
    public GoPluginApiResponse execute() {
        publishArtifact(publishArtifactRequest.getArtifactStore(), publishArtifactRequest.getArtifactPlan());
        return DefaultGoPluginApiResponse.success(publishArtifactResponse.toJSON());
    }

    private void publishArtifact(ArtifactStore artifactStore, ArtifactPlan artifactPlan) {
        final ArtifactStoreConfig artifactStoreConfig = artifactStore.getArtifactStoreConfig();
        try {
            final DockerClient docker = clientFactory.docker(artifactStoreConfig);
            final DockerImage image = artifactPlan.getArtifactPlanConfig().imageToPush(publishArtifactRequest.getAgentWorkingDir());

            LOG.info(format("Pushing docker image `%s` to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));
            consoleLogger.info(format("Pushing docker image `%s` to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));

            final DockerProgressHandler progressHandler = new DockerProgressHandler(consoleLogger);
            docker.push(image.toString(), progressHandler);
            docker.close();

            if (progressHandler.getErrors().isEmpty()) {
                publishArtifactResponse.addMetadata("image", image.toString());
                publishArtifactResponse.addMetadata("digest", progressHandler.getDigest());
                consoleLogger.info(format("Image `%s` successfully pushed to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));
            } else {
                throw new RuntimeException(String.join("\n", progressHandler.getErrors()));
            }

        } catch (Exception e) {
            consoleLogger.error(String.format("Failed to publish %s: %s", artifactPlan, e));
            LOG.error(String.format("Failed to publish %s: %s", artifactPlan, e.getMessage()), e);
            publishArtifactResponse.addError(String.format("Failed to publish %s: %s", artifactPlan, e));
        }
    }
}
