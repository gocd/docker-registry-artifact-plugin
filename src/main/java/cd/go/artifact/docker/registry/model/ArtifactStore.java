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

public class ArtifactStore {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("configuration")
    private ArtifactStoreConfig artifactStoreConfig;

    public ArtifactStore() {
    }

    public ArtifactStore(String id, ArtifactStoreConfig artifactStoreConfig) {
        this.id = id;
        this.artifactStoreConfig = artifactStoreConfig;
    }

    public String getId() {
        return id;
    }

    public ArtifactStoreConfig getArtifactStoreConfig() {
        return artifactStoreConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactStore)) return false;

        ArtifactStore that = (ArtifactStore) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return artifactStoreConfig != null ? artifactStoreConfig.equals(that.artifactStoreConfig) : that.artifactStoreConfig == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (artifactStoreConfig != null ? artifactStoreConfig.hashCode() : 0);
        return result;
    }
}
