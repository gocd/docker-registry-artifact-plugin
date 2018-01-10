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

package cd.go.artifact.docker.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PublishArtifactConfigTest {

    @Test
    public void shouldDeserializeRequestBody() {
        final String json = "{\n" +
                "  \"agent_working_directory\": \"/temp\",\n" +
                "  \"artifact_infos\": [\n" +
                "    {\n" +
                "      \"id\": \"hub.docker\",\n" +
                "      \"configuration\": {\n" +
                "        \"RegistryURL\": \"public-registry-url\",\n" +
                "        \"Username\": \"username\",\n" +
                "        \"Password\": \"password\"\n" +
                "      },\n" +
                "      \"artifact_plans\": [\n" +
                "        {\n" +
                "          \"id\": \"alpine\",\n" +
                "          \"storeId\": \"hub.docker\",\n" +
                "          \"configuration\": {\n" +
                "            \"BuildFile\": \"alpine-build.json\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"centos\",\n" +
                "          \"storeId\": \"hub.docker\",\n" +
                "          \"configuration\": {\n" +
                "            \"BuildFile\": \"centos-build.json\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        final PublishArtifactConfig publishArtifactConfig = PublishArtifactConfig.fromJSON(json);

        assertThat(publishArtifactConfig.getArtifactInfos()).hasSize(1);
        assertThat(publishArtifactConfig.getAgentWorkingDir()).isEqualTo("/temp");

        final ArtifactInfo artifactInfo = publishArtifactConfig.getArtifactInfos().get(0);
        assertThat(artifactInfo.getId()).isEqualTo("hub.docker");
        assertThat(artifactInfo.getArtifactStoreConfig()).isEqualTo(new ArtifactStoreConfig("public-registry-url", "username", "password"));

        assertThat(artifactInfo.getArtifactPlans()).hasSize(2);
        assertThat(artifactInfo.getArtifactPlans()).containsExactlyInAnyOrder(
                new ArtifactPlan("alpine", "hub.docker", "alpine-build.json"),
                new ArtifactPlan("centos", "hub.docker", "centos-build.json")
        );
    }
}