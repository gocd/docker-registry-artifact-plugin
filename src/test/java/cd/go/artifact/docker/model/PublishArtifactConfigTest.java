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
                "  \"artifact_infos\": [\n" +
                "    {\n" +
                "      \"configuration\": {\n" +
                "        \"Foo\": \"Bar\"\n" +
                "      },\n" +
                "      \"id\": \"s3-store\",\n" +
                "      \"artifact_plans\": [\n" +
                "        {\n" +
                "          \"configuration\": {\n" +
                "            \"BuildFile\": \"build-file.json\"\n" +
                "          },\n" +
                "          \"id\": \"alpine\",\n" +
                "          \"storeId\": \"s3-store\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"configuration\": {\n" +
                "            \"BuildFile\": \"build-file2.json\"\n" +
                "          },\n" +
                "          \"id\": \"centos\",\n" +
                "          \"storeId\": \"s3-store\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"agent_working_directory\": \"/temp\"\n" +
                "}";

        final PublishArtifactConfig publishArtifactConfig = PublishArtifactConfig.fromJSON(json);

        assertThat(publishArtifactConfig.getArtifactInfos()).hasSize(1);
        assertThat(publishArtifactConfig.getAgentWorkingDir()).isEqualTo("/temp");

        final ArtifactInfo artifactInfo = publishArtifactConfig.getArtifactInfos().get(0);
        assertThat(artifactInfo.getId()).isEqualTo("s3-store");
        assertThat(artifactInfo.getArtifactStoreConfig()).isEqualTo(new ArtifactStoreConfig(null, null, null));

        assertThat(artifactInfo.getArtifactPlans()).hasSize(2);
        assertThat(artifactInfo.getArtifactPlans()).containsExactlyInAnyOrder(
                new ArtifactPlan("alpine", "s3-store", new ArtifactPlanConfig("build-file.json")),
                new ArtifactPlan("centos", "s3-store", new ArtifactPlanConfig("build-file2.json"))
        );
    }
}