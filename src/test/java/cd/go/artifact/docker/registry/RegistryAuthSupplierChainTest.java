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

import cd.go.artifact.docker.registry.RegistryAuthSupplierChain;
import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import com.spotify.docker.client.messages.RegistryAuth;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistryAuthSupplierChainTest {
    @Test
    public void shouldBuildRegistryAuthSupplierFromArtifactStoreConfig() {
        final ArtifactStoreConfig artifactStoreConfig = new ArtifactStoreConfig("registry-url", "username", "password");

        final RegistryAuthSupplierChain registryAuthSupplierChain = new RegistryAuthSupplierChain(artifactStoreConfig);

        final RegistryAuth registryAuth = registryAuthSupplierChain.authFor("foo");
        assertThat(registryAuth.serverAddress()).isEqualTo(artifactStoreConfig.getRegistryUrl());
        assertThat(registryAuth.username()).isEqualTo(artifactStoreConfig.getUsername());
        assertThat(registryAuth.password()).isEqualTo(artifactStoreConfig.getPassword());
    }
}