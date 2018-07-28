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

package cd.go.artifact.docker.registry.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtifactPlan {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("storeId")
    private String storeId;

    @Expose
    @SerializedName("configuration")
    private ArtifactPlanConfig artifactPlanConfig;

    public ArtifactPlan() {
    }

    public ArtifactPlan(String id, String storeId, String sourceFile) {
        this.id = id;
        this.storeId = storeId;
        this.artifactPlanConfig = new SourceFileArtifactPlanConfig(sourceFile);
    }

    public String getId() {
        return id;
    }

    public String getStoreId() {
        return storeId;
    }

    public ArtifactPlanConfig getArtifactPlanConfig() {
        return artifactPlanConfig;
    }

    @Override
    public String toString() {
        return String.format("Artifact[id=%s, storeId=%s, artifactPlanConfig=%s]", getId(), getStoreId(), artifactPlanConfig.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactPlan)) return false;

        ArtifactPlan that = (ArtifactPlan) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
        return artifactPlanConfig != null ? artifactPlanConfig.equals(that.artifactPlanConfig) : that.artifactPlanConfig == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (artifactPlanConfig != null ? artifactPlanConfig.hashCode() : 0);
        return result;
    }
}
