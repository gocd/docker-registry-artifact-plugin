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

import cd.go.artifact.docker.registry.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GetPluginIconExecutorTest {
    @Test
    public void rendersIconInBase64() {
        GoPluginApiResponse response = new GetPluginIconExecutor().execute();
        Map<String, String> hashMap = new Gson().fromJson(response.responseBody(), new TypeToken<Map<String,String>>(){}.getType());
        assertThat(hashMap).hasSize(2);
        assertThat(hashMap.get("content_type")).isEqualTo("image/svg+xml");
        assertThat(Util.readResourceBytes("/plugin-icon.svg")).isEqualTo(Base64.getDecoder().decode(hashMap.get("data")));
    }
}