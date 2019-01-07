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

package cd.go.artifact.docker.registry;

import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import com.amazonaws.services.ecr.AmazonECRClient;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;

import static cd.go.artifact.docker.registry.DockerRegistryArtifactPlugin.LOG;
import static java.text.MessageFormat.format;

public class DockerClientFactory {
    private static final DockerClientFactory DOCKER_CLIENT_FACTORY = new DockerClientFactory();

    public DockerClient docker(ArtifactStoreConfig artifactStoreConfig) throws InterruptedException, DockerException, DockerCertificateException {
        return createClient(artifactStoreConfig);
    }

    public static DockerClientFactory instance() {
        return DOCKER_CLIENT_FACTORY;
    }

    private static DefaultDockerClient createClient(ArtifactStoreConfig artifactStoreConfig) throws DockerCertificateException, DockerException, InterruptedException {
        final RegistryAuthSupplierChain registryAuthSupplier = new RegistryAuthSupplierChain(artifactStoreConfig, AmazonECRClient.builder());
        DefaultDockerClient docker = DefaultDockerClient.fromEnv().registryAuthSupplier(registryAuthSupplier).build();

        LOG.info(format("Using docker registry server `{0}`.", artifactStoreConfig.getRegistryUrl()));

        final String result = docker.ping();
        if (!result.equalsIgnoreCase("OK")) {
            throw new RuntimeException("Could not ping the docker server.");
        }
        return docker;
    }
}