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

package cd.go.artifact.docker;

import cd.go.artifact.docker.model.ArtifactStoreConfig;
import io.fabric8.docker.api.model.AuthConfig;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;

import java.util.Collections;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static java.text.MessageFormat.format;

public class DockerClientFactory {
    private static final DockerClientFactory DOCKER_CLIENT_FACTORY = new DockerClientFactory();

    public synchronized DockerClient docker(ArtifactStoreConfig artifactStoreConfig) throws Exception {
        return createClient(artifactStoreConfig);
    }

    public static DockerClientFactory instance() {
        return DOCKER_CLIENT_FACTORY;
    }

    private static DefaultDockerClient createClient(ArtifactStoreConfig artifactStoreConfig) {
        Config config = new ConfigBuilder()
                .withAuthConfigs(Collections.singletonMap(artifactStoreConfig.getRegistryUrl(), new AuthConfig(null, null, artifactStoreConfig.getPassword(), artifactStoreConfig.getRegistryUrl(), artifactStoreConfig.getUsername())))
                .build();

        LOG.info(format("Using docker registry server `{0}`.", artifactStoreConfig.getRegistryUrl()));

        DefaultDockerClient docker = new DefaultDockerClient(config);
        docker.ping();
        if (!docker.ping()) {
            throw new RuntimeException("Could not ping the docker server.");
        }
        return docker;
    }
}