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

package cd.go.artifact.docker.registry.executors;

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class GetArtifactStoreConfigMetadataExecutorTest {
    @Test
    public void shouldReturnArtifactStoreMetadata() throws JSONException {
        final GoPluginApiResponse response = new GetArtifactStoreConfigMetadataExecutor().execute();

        final String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"RegistryType\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": true,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"RegistryURL\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"RegistryID\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"AWSAccessKeyId\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": true\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"AWSSecretAccessKey\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": true\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"AWSRegion\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"Username\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": false\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"Password\",\n" +
                "    \"metadata\": {\n" +
                "      \"required\": false,\n" +
                "      \"secure\": true\n" +
                "    }\n" +
                "  }\n" +
                "]";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}