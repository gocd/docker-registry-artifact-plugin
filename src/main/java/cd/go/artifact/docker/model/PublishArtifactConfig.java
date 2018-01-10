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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import static cd.go.artifact.docker.utils.Util.GSON;

public class PublishArtifactConfig {
    @Expose
    @SerializedName("agent_working_directory")
    private String agentWorkingDir;

    @Expose
    @SerializedName("artifact_infos")
    private List<ArtifactInfo> artifactInfos;

    public PublishArtifactConfig() {
    }

    public PublishArtifactConfig(String agentWorkingDir, ArtifactInfo... artifactInfos) {
        this.agentWorkingDir = agentWorkingDir;
        this.artifactInfos = Arrays.asList(artifactInfos);
    }

    public String getAgentWorkingDir() {
        return agentWorkingDir;
    }

    public List<ArtifactInfo> getArtifactInfos() {
        return artifactInfos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublishArtifactConfig)) return false;

        PublishArtifactConfig that = (PublishArtifactConfig) o;

        if (agentWorkingDir != null ? !agentWorkingDir.equals(that.agentWorkingDir) : that.agentWorkingDir != null)
            return false;
        return artifactInfos != null ? artifactInfos.equals(that.artifactInfos) : that.artifactInfos == null;
    }

    @Override
    public int hashCode() {
        int result = agentWorkingDir != null ? agentWorkingDir.hashCode() : 0;
        result = 31 * result + (artifactInfos != null ? artifactInfos.hashCode() : 0);
        return result;
    }

    public static PublishArtifactConfig fromJSON(String json) {
        return GSON.fromJson(json, PublishArtifactConfig.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }
}
