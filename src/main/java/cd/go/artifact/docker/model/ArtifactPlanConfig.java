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

package cd.go.artifact.docker.model;

import cd.go.artifact.docker.annotation.ProfileField;
import cd.go.artifact.docker.annotation.Validatable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static cd.go.artifact.docker.utils.Util.GSON;

public class ArtifactPlanConfig implements Validatable {
    @Expose
    @SerializedName("BuildFile")
    @ProfileField(key = "BuildFile", required = true, secure = false)
    private String buildFile;

    public ArtifactPlanConfig() {
    }

    public ArtifactPlanConfig(String buildFile) {
        this.buildFile = buildFile;
    }

    public String getBuildFile() {
        return buildFile;
    }

    public Map<String, String> getImageAndTag(String agentWorkingDirectory) throws IOException {
        final byte[] bytes = Files.readAllBytes(Paths.get(agentWorkingDirectory, getBuildFile()));
        return GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    @Override
    public String toString() {
        return toJSON();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactPlanConfig)) return false;

        ArtifactPlanConfig that = (ArtifactPlanConfig) o;

        return buildFile != null ? buildFile.equals(that.buildFile) : that.buildFile == null;
    }

    @Override
    public int hashCode() {
        return buildFile != null ? buildFile.hashCode() : 0;
    }

    public static ArtifactPlanConfig fromJSON(String json) {
        return GSON.fromJson(json, ArtifactPlanConfig.class);
    }
}
