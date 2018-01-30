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

package cd.go.artifact.docker.registry;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsoleLoggerTest {
    private static GoApplicationAccessor accessor;
    private static ConsoleLogger consoleLogger;
    private static ArgumentCaptor<GoApiRequest> argumentCaptor;

    @BeforeClass
    public static void setUp() {
        accessor = mock(GoApplicationAccessor.class);
        argumentCaptor = ArgumentCaptor.forClass(GoApiRequest.class);

        consoleLogger = ConsoleLogger.getLogger(accessor);

        when(accessor.submit(argumentCaptor.capture())).thenReturn(DefaultGoApiResponse.success(null));
    }

    @Test
    public void shouldLogInfoMessageToConsoleLog() throws JSONException {
        consoleLogger.info("This is info message.");

        final GoApiRequest request = argumentCaptor.getValue();
        assertThat(request.api()).isEqualTo(Constants.SEND_CONSOLE_LOG);
        assertThat(request.apiVersion()).isEqualTo(Constants.API_VERSION);

        final String expectedJSON = "{\n" +
                "  \"logLevel\": \"INFO\",\n" +
                "  \"message\": \"This is info message.\"\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, request.requestBody(), true);
    }

    @Test
    public void shouldLogErrorMessageToConsoleLog() throws JSONException {
        consoleLogger.error("This is error.");

        final GoApiRequest request = argumentCaptor.getValue();
        assertThat(request.api()).isEqualTo(Constants.SEND_CONSOLE_LOG);
        assertThat(request.apiVersion()).isEqualTo(Constants.API_VERSION);

        final String expectedJSON = "{\n" +
                "  \"logLevel\": \"ERROR\",\n" +
                "  \"message\": \"This is error.\"\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, request.requestBody(), true);
    }
}