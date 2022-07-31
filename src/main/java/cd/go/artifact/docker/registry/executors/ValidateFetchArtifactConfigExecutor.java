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

import cd.go.artifact.docker.registry.model.FetchArtifactConfig;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class ValidateFetchArtifactConfigExecutor implements RequestExecutor {

    private final FetchArtifactConfig fetchArtifactConfig;

    public ValidateFetchArtifactConfigExecutor(GoPluginApiRequest request) {
        this.fetchArtifactConfig = FetchArtifactConfig.fromJSON(request.requestBody());
    }

    @Override
    public GoPluginApiResponse execute() {
        return DefaultGoPluginApiResponse.success(fetchArtifactConfig.validate().toJSON());
    }
}
