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
import cd.go.artifact.docker.model.*;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.OutputHandle;

import java.util.List;
import java.util.Map;

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
        try {
            publish(publishArtifactConfig);
        } catch (Exception e) {
            publishArtifactResponse.addError(String.format("Could not upload artifact: %s", e.getMessage()));
            LOG.error(String.format("Could not upload artifact: %%s%s", e));
        }
        return DefaultGoPluginApiResponse.success(publishArtifactResponse.toJSON());
    }

    private void publish(PublishArtifactConfig publishArtifactConfigs) throws Exception {
        if (publishArtifactConfigs == null || publishArtifactConfigs.getArtifactInfos().isEmpty()) {
            publishArtifactResponse.addError("No artifact to publish.");
            return;
        }

        for (ArtifactInfo artifactInfo : publishArtifactConfigs.getArtifactInfos()) {
            publishArtifactsToArtifactStore(artifactInfo);
        }
    }

    private void publishArtifactsToArtifactStore(ArtifactInfo artifactInfo) throws Exception {
        final ArtifactStoreConfig artifactStoreConfig = artifactInfo.getArtifactStoreConfig();
        final List<ArtifactPlan> artifactPlans = artifactInfo.getArtifactPlans();

        for (ArtifactPlan artifactPlan : artifactPlans) {
            publishArtifact(artifactStoreConfig, artifactPlan);
        }
    }

    private void publishArtifact(ArtifactStoreConfig artifactStoreConfig, ArtifactPlan artifactPlan) throws Exception {
        final Map<String, String> buildFile = artifactPlan.getArtifactPlanConfig().getImageAndTag(publishArtifactConfig.getAgentWorkingDir());
        final DockerClient docker = clientFactory.docker(artifactStoreConfig);

        LOG.info(format("Uploading artifact using %s to artifact store with id `%s`.", artifactPlan, artifactPlan.getStoreId()));
        final String imageToPush = String.format("%s:%s", buildFile.get("image"), buildFile.get("tag"));


        final DockerEventListener dockerEventListener = new DockerEventListener();
        OutputHandle handle = docker.image().withName(buildFile.get("image")).push()
                .usingListener(dockerEventListener)
                .withTag(buildFile.get("tag"))
                .toRegistry();

        dockerEventListener.await();
        handle.close();
        docker.close();
        publishArtifactResponse.addMetadata("docker-image", imageToPush);
    }
}
