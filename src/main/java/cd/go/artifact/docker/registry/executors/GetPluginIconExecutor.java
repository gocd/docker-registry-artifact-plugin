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
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Base64;

public class GetPluginIconExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();

    @Override
    public GoPluginApiResponse execute() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content_type", getContentType());
        jsonObject.addProperty("data", Base64.getEncoder().encodeToString(Util.readResourceBytes(getIcon())));
        return DefaultGoPluginApiResponse.success(GSON.toJson(jsonObject));
    }

    private String getContentType() {
        return "image/svg+xml";
    }

    private String getIcon() {
        return "/plugin-icon.svg";
    }
}
