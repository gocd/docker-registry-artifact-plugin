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

import cd.go.artifact.docker.registry.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GetFetchArtifactViewExecutorTest {

    @Test
    public void shouldRenderTheTemplateInJSON() {
        GoPluginApiResponse response = new GetFetchArtifactViewExecutor().execute();

        Map<String, String> responseHash = new Gson().fromJson(response.responseBody(), new TypeToken<Map<String,String>>(){}.getType());

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(responseHash).containsEntry("template", Util.readResource("/fetch-artifact.template.html"));
    }
}