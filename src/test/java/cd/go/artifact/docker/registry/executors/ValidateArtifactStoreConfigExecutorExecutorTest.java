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

import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ValidateArtifactStoreConfigExecutorExecutorTest {
    @Mock
    private GoPluginApiRequest request;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        when(request.requestBody()).thenReturn("{}");

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"S3Bucket\",\n" +
                "    \"message\": \"S3Bucket must not be blank.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"AWSAccessKey\",\n" +
                "    \"message\": \"AWSAccessKey must not be blank.\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"key\": \"AWSSecretAccessKey\",\n" +
                "    \"message\": \"AWSSecretAccessKey must not be blank.\"\n" +
                "  }\n" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateProperData() throws JSONException {
        String requestBody = new JSONObject()
                .put("S3Bucket", "http://localhost/index")
                .put("AWSAccessKey", "chuck-norris")
                .put("AWSSecretAccessKey", "chuck-norris-doesnt-need-passwords")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateArtifactStoreConfigExecutor(request).execute();
        String expectedJSON = "[]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }
}
