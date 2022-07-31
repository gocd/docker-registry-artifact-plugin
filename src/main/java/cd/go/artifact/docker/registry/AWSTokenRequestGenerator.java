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

package cd.go.artifact.docker.registry;

import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.services.ecr.AmazonECR;
import com.amazonaws.services.ecr.AmazonECRClient;
import com.amazonaws.services.ecr.AmazonECRClientBuilder;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenRequest;
import com.amazonaws.services.ecr.model.GetAuthorizationTokenResult;
import com.thoughtworks.go.plugin.api.logging.Logger;

import java.util.Base64;

import static org.apache.commons.lang.StringUtils.isNotBlank;

class AWSTokenRequestGenerator {
    private static final Logger LOG = Logger.getLoggerFor(AWSTokenRequestGenerator.class);
    private AwsSyncClientBuilder<AmazonECRClientBuilder, AmazonECR> builder;

    AWSTokenRequestGenerator() {
        builder = AmazonECRClient.builder();
    }

    AWSTokenRequestGenerator(AwsSyncClientBuilder<AmazonECRClientBuilder, AmazonECR> builder) {
        this.builder = builder;
    }

    AwsSyncClientBuilder<AmazonECRClientBuilder, AmazonECR> getBuilder() {
        return builder;
    }

    String[] getUsernameAndPasswordFromECRToken(ArtifactStoreConfig artifactStoreConfig) {
        builder.setRegion(artifactStoreConfig.getAwsRegion());
        setCredentialsProvider(artifactStoreConfig);
        GetAuthorizationTokenResult authorizationTokenResult = builder.build().getAuthorizationToken(new GetAuthorizationTokenRequest().withRegistryIds(artifactStoreConfig.getRegistryId()));
        String authorizationToken = authorizationTokenResult.getAuthorizationData().get(0).getAuthorizationToken();
        return new String(Base64.getDecoder().decode(authorizationToken)).split(":");
    }

    void setCredentialsProvider(ArtifactStoreConfig artifactStoreConfig) {
        if (isNotBlank(artifactStoreConfig.getAwsAccessKeyId()) || isNotBlank(artifactStoreConfig.getAwsSecretAccessKey())) {
            AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(artifactStoreConfig.getAwsAccessKeyId(), artifactStoreConfig.getAwsSecretAccessKey()));
            builder.setCredentials(awsStaticCredentialsProvider);
            LOG.debug("Using the AWS keys configured in the specified artifact store.");
        }
        else {
            builder.setCredentials(new DefaultAWSCredentialsProviderChain());
            LOG.debug("Setting the default aws credentials chain provider. This happens if the specified artifact store does not have aws keys configured. The default chain checks for environment variables, system properties, credentials profile, instance profile.");
        }
    }

}
