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
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.services.ecr.AmazonECR;
import com.amazonaws.services.ecr.AmazonECRClient;
import com.amazonaws.services.ecr.AmazonECRClientBuilder;
import com.amazonaws.services.ecr.model.AuthorizationData;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult;
import com.spotify.docker.client.messages.RegistryAuth;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistryAuthSupplierChainTest {
    private AmazonECRClient mockAmazonEcrClient = mock(AmazonECRClient.class);

    @Test
    public void shouldBuildRegistryAuthSupplierFromArtifactStoreConfigIfTypeIsOther() {
        final ArtifactStoreConfig artifactStoreConfig = new ArtifactStoreConfig("registry-url", "other", "username", "password");

        final RegistryAuthSupplierChain registryAuthSupplierChain = new RegistryAuthSupplierChain(artifactStoreConfig, new AWSTokenRequestGenerator());

        final RegistryAuth registryAuth = registryAuthSupplierChain.authFor("foo");
        assertThat(registryAuth.serverAddress()).isEqualTo(artifactStoreConfig.getRegistryUrl());
        assertThat(registryAuth.username()).isEqualTo(artifactStoreConfig.getUsername());
        assertThat(registryAuth.password()).isEqualTo(artifactStoreConfig.getPassword());
    }

    @Test
    public void shouldSetUsernameAndPasswordByMakingARequestToECRIfTypeIsEcr() {
        GetAuthorizationTokenResult mockAuthorizationTokenResult = mock(GetAuthorizationTokenResult.class);
        AuthorizationData mockAuthorization = mock(AuthorizationData.class);
        List<AuthorizationData> authorizationData = new ArrayList<>();
        authorizationData.add(mockAuthorization);
        final ArtifactStoreConfig artifactStoreConfig = new ArtifactStoreConfig("https://12345.dkr.ecr.region.amazonaws.com", "ecr", "awsAccessKeyId", "awsSecretAccessKey", "awsRegion");

        String usernameAndPassword="AWS:secretAuthorizationToken";
        when(mockAmazonEcrClient.getAuthorizationToken(any(GetAuthorizationTokenRequest.class))).thenReturn(mockAuthorizationTokenResult);
        when(mockAuthorizationTokenResult.getAuthorizationData()).thenReturn(authorizationData);
        when(mockAuthorization.getAuthorizationToken()).thenReturn(Base64.getEncoder().encodeToString(usernameAndPassword.getBytes()));

        final RegistryAuthSupplierChain registryAuthSupplierChain = new RegistryAuthSupplierChain(artifactStoreConfig, new AWSTokenRequestGenerator(new MockAwsECRClientBuilder(new ClientConfigurationFactory())));
        final RegistryAuth registryAuth = registryAuthSupplierChain.authFor("foo");

        assertThat(registryAuth.serverAddress()).isEqualTo(artifactStoreConfig.getRegistryUrl());
        assertThat(registryAuth.username()).isEqualTo("AWS");
        assertThat(registryAuth.password()).isEqualTo("secretAuthorizationToken");
    }

    public class MockAwsECRClientBuilder extends AwsSyncClientBuilder<AmazonECRClientBuilder, AmazonECR> {

        MockAwsECRClientBuilder(ClientConfigurationFactory clientConfigFactory) {
            super(clientConfigFactory);
        }

        @Override
        protected AmazonECR build(AwsSyncClientParams clientParams) {
            return mockAmazonEcrClient;
        }
    }
}