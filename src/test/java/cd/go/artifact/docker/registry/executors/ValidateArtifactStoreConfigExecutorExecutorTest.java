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

package cd.go.artifact.docker.registry.executors;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ValidateArtifactStoreConfigExecutorExecutorTest {
    @Mock
    private GoPluginApiRequest request;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        when(request.requestBody()).thenReturn("{}");

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"RegistryType\",\n" +
                "    \"message\": \"RegistryType must not be blank.\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateProperDataIfTypeIsOther() throws JSONException {
        String requestBody = new JSONObject()
                .put("RegistryURL", "http://localhost/index")
                .put("RegistryType", "other")
                .put("Username", "chuck-norris")
                .put("Password", "chuck-norris-doesnt-need-passwords")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();
        String expectedJSON = "[]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateProperDataIfTypeIsEcr() throws JSONException {
        String requestBody = new JSONObject()
                .put("RegistryID", "12345")
                .put("RegistryType", "ecr")
                .put("AWSAccessKeyId", "chuck-norris-aws-access-key-id")
                .put("AWSSecretAccessKey", "chuck-norris-aws-secret-access-key")
                .put("AWSRegion", "us-west-1")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();
        String expectedJSON = "[]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateRegistryType() throws JSONException {
        String requestBody = new JSONObject()
                .put("RegistryURL", "http://localhost/index")
                .put("RegistryType", "foo")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();
        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"RegistryType\",\n" +
                "    \"message\": \"RegistryType must either be `ecr` or `other`.\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidatePresenceOfUsernameAndPasswordIfTypeIsOther() throws JSONException {
        String requestBody = new JSONObject()
                .put("RegistryURL", "http://localhost/index")
                .put("RegistryType", "other")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();
        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"Username\",\n" +
                "    \"message\": \"Username must not be blank.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"Password\",\n" +
                "    \"message\": \"Password must not be blank.\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidatePresenceOfAwsRegionIfTypeIsEcr() throws JSONException {
        String requestBody = new JSONObject()
                .put("RegistryType", "ecr")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();
        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"AWSRegion\",\n" +
                "    \"message\": \"AWSRegion must not be blank.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"RegistryID\",\n" +
                "    \"message\": \"RegistryID must not be blank.\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }
}
