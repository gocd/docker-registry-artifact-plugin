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

import cd.go.artifact.docker.registry.annotation.ValidationError;
import cd.go.artifact.docker.registry.annotation.ValidationResult;
import cd.go.artifact.docker.registry.model.ArtifactPlanConfig;
import com.google.gson.JsonParseException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class ValidatePublishArtifactConfigExecutor implements RequestExecutor {
    private final String artifactPlanConfigJSON;

    public ValidatePublishArtifactConfigExecutor(GoPluginApiRequest request) {
        artifactPlanConfigJSON = request.requestBody();
    }

    @Override
    public GoPluginApiResponse execute() {
        try {
            ArtifactPlanConfig artifactPlanConfig = ArtifactPlanConfig.fromJSON(artifactPlanConfigJSON);
            ValidationResult validationResult = artifactPlanConfig.validate();
            return DefaultGoPluginApiResponse.success(validationResult.toJSON());
        } catch (JsonParseException e) {
            ValidationResult validationResult = new ValidationResult(
                    new ValidationError("Source", "Source file must be specified."));
            return DefaultGoPluginApiResponse.success(validationResult.toJSON());
        }
    }
}
