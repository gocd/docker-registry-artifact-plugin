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

package cd.go.artifact.docker.executors;

import cd.go.artifact.docker.model.ArtifactStoreConfig;
import cd.go.artifact.docker.model.FetchArtifactConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Map;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static cd.go.artifact.docker.utils.Util.GSON;

public class FetchArtifactExecutor implements RequestExecutor {
    private FetchArtifactRequest fetchArtifactRequest;

    public FetchArtifactExecutor(GoPluginApiRequest request) {
        this.fetchArtifactRequest = FetchArtifactRequest.fromJSON(request.requestBody());
    }

    @Override
    public GoPluginApiResponse execute() {
        fetch(fetchArtifactRequest);
        return DefaultGoPluginApiResponse.success("");
    }

    private void fetch(FetchArtifactRequest fetchArtifactRequest) {
        LOG.info(String.format("Fetching artifact %s", fetchArtifactRequest.getFetchArtifactConfig()));
        try {
            throw new RuntimeException("Implement me!");
        } catch (Exception e) {
            LOG.error("Failed to download artifact s" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static class FetchArtifactRequest {
        @Expose
        @SerializedName("store_configuration")
        private ArtifactStoreConfig artifactStoreConfig;
        @Expose
        @SerializedName("fetch_artifact_configuration")
        private FetchArtifactConfig fetchArtifactConfig;
        @Expose
        @SerializedName("artifact_metadata")
        private Map<String, Object> metadata;

        public ArtifactStoreConfig getArtifactStoreConfig() {
            return artifactStoreConfig;
        }

        public FetchArtifactConfig getFetchArtifactConfig() {
            return fetchArtifactConfig;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public static FetchArtifactRequest fromJSON(String json) {
            return GSON.fromJson(json, FetchArtifactRequest.class);
        }
    }
}
