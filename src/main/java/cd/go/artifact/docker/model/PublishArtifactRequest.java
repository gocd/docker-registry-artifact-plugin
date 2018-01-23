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

import static cd.go.artifact.docker.utils.Util.GSON;

public class PublishArtifactRequest {
    @Expose
    @SerializedName("agent_working_directory")
    private String agentWorkingDir;

    @Expose
    @SerializedName("artifact_store")
    private ArtifactStore artifactStore;

    @Expose
    @SerializedName("artifact_plan")
    private ArtifactPlan artifactPlan;

    public PublishArtifactRequest() {
    }

    public PublishArtifactRequest(ArtifactStore artifactStore, ArtifactPlan artifactPlan, String agentWorkingDir) {
        this.agentWorkingDir = agentWorkingDir;
        this.artifactStore = artifactStore;
        this.artifactPlan = artifactPlan;
    }

    public String getAgentWorkingDir() {
        return agentWorkingDir;
    }

    public ArtifactStore getArtifactStore() {
        return artifactStore;
    }

    public ArtifactPlan getArtifactPlan() {
        return artifactPlan;
    }

    public static PublishArtifactRequest fromJSON(String json) {
        return GSON.fromJson(json, PublishArtifactRequest.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublishArtifactRequest)) return false;

        PublishArtifactRequest that = (PublishArtifactRequest) o;

        if (agentWorkingDir != null ? !agentWorkingDir.equals(that.agentWorkingDir) : that.agentWorkingDir != null)
            return false;
        if (artifactStore != null ? !artifactStore.equals(that.artifactStore) : that.artifactStore != null)
            return false;
        return artifactPlan != null ? artifactPlan.equals(that.artifactPlan) : that.artifactPlan == null;
    }

    @Override
    public int hashCode() {
        int result = agentWorkingDir != null ? agentWorkingDir.hashCode() : 0;
        result = 31 * result + (artifactStore != null ? artifactStore.hashCode() : 0);
        result = 31 * result + (artifactPlan != null ? artifactPlan.hashCode() : 0);
        return result;
    }
}
