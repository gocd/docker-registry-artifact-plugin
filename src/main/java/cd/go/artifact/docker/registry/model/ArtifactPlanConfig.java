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

package cd.go.artifact.docker.registry.model;

import cd.go.artifact.docker.registry.annotation.Validatable;
import cd.go.artifact.docker.registry.utils.Util;

import java.util.Map;

public abstract class ArtifactPlanConfig implements Validatable {

    abstract public DockerImage imageToPush(String agentWorkingDirectory, Map<String, String> environmentVariables) throws UnresolvedPropertyException;

    @Override
    public String toString() {
        return toJSON();
    }

    public static ArtifactPlanConfig fromJSON(String json) {
        return Util.GSON.fromJson(json, ArtifactPlanConfig.class);
    }
}
