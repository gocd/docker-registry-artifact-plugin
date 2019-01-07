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
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.services.ecr.AmazonECR;
import com.amazonaws.services.ecr.AmazonECRClientBuilder;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult;
import com.spotify.docker.client.auth.RegistryAuthSupplier;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;

import java.util.Base64;
import java.util.Collections;

public class RegistryAuthSupplierChain implements RegistryAuthSupplier {
    private final RegistryAuth registryAuth;

    public RegistryAuthSupplierChain(ArtifactStoreConfig artifactStoreConfig, AwsSyncClientBuilder<AmazonECRClientBuilder, AmazonECR> builder) {
        String username, password;
        if (artifactStoreConfig.isRegistryTypeEcr()) {
            builder.setRegion(artifactStoreConfig.getAwsRegion());
            builder.setCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(artifactStoreConfig.getAwsAccessKeyId(), artifactStoreConfig.getAwsSecretAccessKey())));
            GetAuthorizationTokenResult authorizationTokenResult = builder.build().getAuthorizationToken(new GetAuthorizationTokenRequest().withRegistryIds(getAwsAccountIdFromURL(artifactStoreConfig.getRegistryUrl())));
            String authorizationToken = authorizationTokenResult.getAuthorizationData().get(0).getAuthorizationToken();
            String[] usernameAndPassword = new String(Base64.getDecoder().decode(authorizationToken)).split(":");
            username = usernameAndPassword[0];
            password = usernameAndPassword[1];
        }
        else {
            username = artifactStoreConfig.getUsername();
            password = artifactStoreConfig.getPassword();
        }
        registryAuth = RegistryAuth.builder()
                .username(username)
                .serverAddress(artifactStoreConfig.getRegistryUrl())
                .password(password).build();
    }

    private String getAwsAccountIdFromURL(String registryUrl) {
        return registryUrl.split("//")[1].split("\\.")[0];
    }

    @Override
    public RegistryAuth authFor(String imageName) {
        return registryAuth;
    }

    @Override
    public RegistryAuth authForSwarm() {
        return registryAuth;
    }

    @Override
    public RegistryConfigs authForBuild() {
        return RegistryConfigs.create(Collections.singletonMap(registryAuth.serverAddress(), registryAuth));
    }
}
