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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ValidatePublishArtifactConfigExecutorTest {
    @Mock
    private GoPluginApiRequest request;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateRequestWithBuildFile() throws Exception {
        String requestBody = new JSONObject().put("BuildFile", "").toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidatePublishArtifactConfigExecutor(request).execute();

        String expectedJSON = "[" +
                "  {" +
                "    'key': 'Source'," +
                "    'message': 'Source file must be specified.'" +
                "  }" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateRequestWithImageAndTag() throws JSONException {
        String requestBody = new JSONObject()
                .put("Source", "")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidatePublishArtifactConfigExecutor(request).execute();

        String expectedJSON = "[" +
                "  {" +
                "    'key': 'Source'," +
                "    'message': 'Source file must be specified.'" +
                "  }" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateInvalidRequest() throws JSONException {
        when(request.requestBody()).thenReturn("{}");

        final GoPluginApiResponse response = new ValidatePublishArtifactConfigExecutor(request).execute();

        String expectedJSON = "[" +
                "  {" +
                "    'key': 'Source'," +
                "    'message': 'Source file must be specified.'" +
                "  }" +
                "]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateRequestContainingAllFields() throws JSONException {
        String requestBody = new JSONObject()
                .put("DummyProp", "build.json")
                .toString();
        when(request.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = new ValidatePublishArtifactConfigExecutor(request).execute();

        String expectedResponse = new JSONArray().put(
                new JSONObject()
                        .put("key", "Source")
                        .put("message", "Source file must be specified.")
        ).toString();

        JSONAssert.assertEquals(expectedResponse, response.responseBody(), true);
    }
}
