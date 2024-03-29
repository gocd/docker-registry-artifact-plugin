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

package cd.go.artifact.docker.registry.model;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PublishArtifactRequestTest {

    @Test
    public void shouldDeserializeRequestBody() {
        final String json = "{\n" +
                "  \"artifact_plan\": {\n" +
                "    \"configuration\": {\n" +
                "      \"BuildFile\": \"alpine-build.json\"\n" +
                "    },\n" +
                "    \"id\": \"installers\",\n" +
                "    \"storeId\": \"s3-store\"\n" +
                "  },\n" +
                "  \"artifact_store\": {\n" +
                "    \"configuration\": {\n" +
                "      \"RegistryURL\": \"public-registry-url\",\n" +
                "      \"RegistryType\": \"other\",\n" +
                "      \"Username\": \"username\",\n" +
                "      \"Password\": \"password\"\n" +
                "    },\n" +
                "    \"id\": \"s3-store\"\n" +
                "  },\n" +
                "  \"agent_working_directory\": \"/temp\"\n" +
                "}";

        final PublishArtifactRequest publishArtifactRequest = PublishArtifactRequest.fromJSON(json);

        assertThat(publishArtifactRequest.getAgentWorkingDir()).isEqualTo("/temp");

        assertThat(publishArtifactRequest.getArtifactStore().getId()).isEqualTo("s3-store");
        assertThat(publishArtifactRequest.getArtifactStore().getArtifactStoreConfig())
                .isEqualTo(new ArtifactStoreConfig("public-registry-url", "other", "username", "password"));

        assertThat(publishArtifactRequest.getArtifactPlan())
                .isEqualTo(new ArtifactPlan("installers", "s3-store", "alpine-build.json"));
    }
}