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
import cd.go.artifact.docker.model.*;
import com.spotify.docker.client.DockerClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.List;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static java.lang.String.format;

public class PublishArtifactExecutor implements RequestExecutor {
    private final PublishArtifactConfig publishArtifactConfig;
    private final PublishArtifactResponse publishArtifactResponse;
    private final DockerClientFactory clientFactory;

    public PublishArtifactExecutor(GoPluginApiRequest request) {
        this(request, DockerClientFactory.instance());
    }

    PublishArtifactExecutor(GoPluginApiRequest request, DockerClientFactory clientFactory) {
        this.publishArtifactConfig = PublishArtifactConfig.fromJSON(request.requestBody());
        this.clientFactory = clientFactory;
        publishArtifactResponse = new PublishArtifactResponse();
    }

    @Override
    public GoPluginApiResponse execute() {
        publish(publishArtifactConfig);
        return DefaultGoPluginApiResponse.success(publishArtifactResponse.toJSON());
    }

    private void publish(PublishArtifactConfig publishArtifactConfigs) {
        if (publishArtifactConfigs == null || publishArtifactConfigs.getArtifactInfos().isEmpty()) {
            publishArtifactResponse.addError("No artifact to publish.");
            return;
        }

        for (ArtifactInfo artifactInfo : publishArtifactConfigs.getArtifactInfos()) {
            publishArtifactsToArtifactStore(artifactInfo);
        }
    }

    private void publishArtifactsToArtifactStore(ArtifactInfo artifactInfo) {
        final ArtifactStoreConfig artifactStoreConfig = artifactInfo.getArtifactStoreConfig();
        final List<ArtifactPlan> artifactPlans = artifactInfo.getArtifactPlans();

        for (ArtifactPlan artifactPlan : artifactPlans) {
            publishArtifact(artifactStoreConfig, artifactPlan);
        }
    }

    private void publishArtifact(ArtifactStoreConfig artifactStoreConfig, ArtifactPlan artifactPlan) {
        try {
            final DockerClient docker = clientFactory.docker(artifactStoreConfig);
            final DockerImage image = artifactPlan.getArtifactPlanConfig().imageToPush(publishArtifactConfig.getAgentWorkingDir());

            LOG.info(format("Pushing docker image `%s` to docker registry `%s`.", image, artifactStoreConfig.getRegistryUrl()));

            if (!image.getImage().startsWith(artifactStoreConfig.getRegistryUrl())) {
                throw new RuntimeException(String.format("Image is not tagged with repository url: %s", artifactStoreConfig.getRegistryUrl()));
            }

            final DockerProgressHandler dockerProgressHandler = new DockerProgressHandler();
            docker.push(image.toString(), dockerProgressHandler);
            docker.close();

            if (dockerProgressHandler.getErrors().isEmpty()) {
                publishArtifactResponse.addMetadata(artifactPlan.getId(), artifactMetadata(image, dockerProgressHandler.getDigest()));
            } else {
                throw new RuntimeException(String.join("\n", dockerProgressHandler.getErrors()));
            }

        } catch (Exception e) {
            LOG.error(String.format("Failed to publish %s: %s", artifactPlan, e.getMessage()), e);
            publishArtifactResponse.addError(String.format("Failed to publish %s: %s", artifactPlan, e));
        }
    }

    private HashMap<String, String> artifactMetadata(DockerImage image, String digest) {
        final HashMap<String, String> artifactMetadata = new HashMap<>();
        artifactMetadata.put("image", image.toString());
        artifactMetadata.put("digest", digest);
        return artifactMetadata;
    }
}
