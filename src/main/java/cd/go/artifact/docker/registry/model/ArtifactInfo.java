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

import java.util.Arrays;
import java.util.List;

import static cd.go.artifact.docker.registry.utils.Util.GSON;

public class ArtifactInfo {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("configuration")
    private ArtifactStoreConfig artifactStoreConfig;

    @Expose
    @SerializedName("artifact_plans")
    private List<ArtifactPlan> artifactPlans;

    public ArtifactInfo() {
    }

    public ArtifactInfo(String id, ArtifactStoreConfig artifactStoreConfig, ArtifactPlan... artifactPlans) {
        this.id = id;
        this.artifactStoreConfig = artifactStoreConfig;
        this.artifactPlans = Arrays.asList(artifactPlans);
    }

    public static ArtifactInfo fromJSON(String json) {
        return GSON.fromJson(json, ArtifactInfo.class);
    }

    public String getId() {
        return id;
    }

    public ArtifactStoreConfig getArtifactStoreConfig() {
        return artifactStoreConfig;
    }

    public List<ArtifactPlan> getArtifactPlans() {
        return artifactPlans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactInfo)) return false;

        ArtifactInfo that = (ArtifactInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (artifactStoreConfig != null ? !artifactStoreConfig.equals(that.artifactStoreConfig) : that.artifactStoreConfig != null)
            return false;
        return artifactPlans != null ? artifactPlans.equals(that.artifactPlans) : that.artifactPlans == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (artifactStoreConfig != null ? artifactStoreConfig.hashCode() : 0);
        result = 31 * result + (artifactPlans != null ? artifactPlans.hashCode() : 0);
        return result;
    }
}
