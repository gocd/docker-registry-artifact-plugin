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
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AWSTokenRequestGeneratorTest {

    @Test
    public void shouldUseAwsStaticCredentialProviderIfAwsKeysAreConfigured() {
        AWSTokenRequestGenerator awsTokenRequestGenerator = new AWSTokenRequestGenerator();
        awsTokenRequestGenerator.setCredentialsProvider(new ArtifactStoreConfig("registry-url", "ecr", "awsAccessKeyId", "awsSecretKey", "awsRegion"));
        AWSCredentialsProvider credentialsProvider = awsTokenRequestGenerator.getBuilder().getCredentials();
        assertThat(credentialsProvider).isInstanceOf(AWSStaticCredentialsProvider.class);
    }

    @Test
    public void shouldUseDefaultCredentialsProvider() {
        AWSTokenRequestGenerator awsTokenRequestGenerator = new AWSTokenRequestGenerator();
        awsTokenRequestGenerator.setCredentialsProvider(new ArtifactStoreConfig("registry-url", "ecr", null, null, "awsRegion"));
        AWSCredentialsProvider credentialsProvider = awsTokenRequestGenerator.getBuilder().getCredentials();
        assertThat(credentialsProvider).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
    }
}