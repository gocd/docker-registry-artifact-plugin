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
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ValidateFetchArtifactConfigExecutorTest {

    @Mock
    private GoPluginApiRequest request;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void shouldEmptyEnvironmentVariablePrefixShouldBeValid() throws Exception {
        String requestBody = new JSONObject().put("EnvironmentVariablePrefix", "").toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateFetchArtifactConfigExecutor(request).execute();

        String expectedJSON = "[]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateValidEnvironmentVariablePrefix() throws Exception {
        String requestBody = new JSONObject().put("EnvironmentVariablePrefix", "ENVIRONMENT_VARIABLE").toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateFetchArtifactConfigExecutor(request).execute();

        String expectedJSON = "[]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateInValidEnvironmentVariablePrefix() throws Exception {
        String requestBody = new JSONObject().put("EnvironmentVariablePrefix", "1ENVIRONMENT_VARIABLE").toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateFetchArtifactConfigExecutor(request).execute();

        String expectedJSON = "[{\"key\":\"EnvironmentVariablePrefix\",\"message\":\"Invalid environment name prefix. Valid prefixes contain characters, numbers, and underscore; and can\\u0027t start with a number.\"}]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateInValidEnvironmentVariablePrefixes() throws Exception {
        String requestBody = new JSONObject().put("EnvironmentVariablePrefix", "ENVIRONMENT VARIABLE").toString();
        when(request.requestBody()).thenReturn(requestBody);

        final GoPluginApiResponse response = new ValidateFetchArtifactConfigExecutor(request).execute();

        String expectedJSON = "[{\"key\":\"EnvironmentVariablePrefix\",\"message\":\"Invalid environment name prefix. Valid prefixes contain characters, numbers, and underscore; and can\\u0027t start with a number.\"}]";
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }


}