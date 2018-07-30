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

import cd.go.artifact.docker.registry.ConsoleLogger;
import cd.go.artifact.docker.registry.S3ClientFactory;
import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import cd.go.artifact.docker.registry.utils.Util;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spotify.docker.client.DockerClient;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import static cd.go.artifact.docker.registry.S3ArtifactPlugin.LOG;
import static java.lang.String.format;

public class FetchArtifactExecutor implements RequestExecutor {
    private FetchArtifactRequest fetchArtifactRequest;
    private final ConsoleLogger consoleLogger;
    private S3ClientFactory clientFactory;

    public FetchArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger) {
        this(request, consoleLogger, S3ClientFactory.instance());
    }

    FetchArtifactExecutor(GoPluginApiRequest request, ConsoleLogger consoleLogger, S3ClientFactory clientFactory) {
        this.fetchArtifactRequest = FetchArtifactRequest.fromJSON(request.requestBody());
        this.consoleLogger = consoleLogger;
        this.clientFactory = clientFactory;
    }

    @Override
    public GoPluginApiResponse execute() {
        try {
            final Map<String, String> artifactMap = fetchArtifactRequest.getMetadata();
            validateMetadata(artifactMap);

            final String sourceFileToGet = artifactMap.get("Source");

            consoleLogger.info(String.format("Retrieving file `%s` from S3 bucket `%s`.", sourceFileToGet, fetchArtifactRequest.getArtifactStoreConfig().getS3bucket()));
            LOG.info(String.format("Retrieving file `%s` from S3 bucket `%s`.", sourceFileToGet, fetchArtifactRequest.getArtifactStoreConfig().getS3bucket()));

            AmazonS3 s3 = clientFactory.s3(fetchArtifactRequest.getArtifactStoreConfig());
            S3Object s3Object = s3.getObject(fetchArtifactRequest.getArtifactStoreConfig().getS3bucket(), sourceFileToGet);
            S3ObjectInputStream s3InputStream = s3Object.getObjectContent();
            FileOutputStream fileOutput = new FileOutputStream(new File(sourceFileToGet));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3InputStream.read(read_buf)) > 0) {
                fileOutput.write(read_buf, 0, read_len);
            }
            s3InputStream.close();
            fileOutput.close();

            consoleLogger.info(String.format("Source `%s` successfully pulled from S3 bucket `%s`.", sourceFileToGet, fetchArtifactRequest.getArtifactStoreConfig().getS3bucket()));

            return DefaultGoPluginApiResponse.success("");
        } catch (Exception e) {
            final String message = format("Failed pull source file: %s", e);
            consoleLogger.error(message);
            LOG.error(message);
            return DefaultGoPluginApiResponse.error(message);
        }
    }

    public void validateMetadata(Map<String, String> artifactMap) {
        if (artifactMap == null) {
            throw new RuntimeException(String.format("Cannot fetch the source file from S3: Invalid metadata received from the GoCD server. The artifact metadata is null."));
        }

        if (!artifactMap.containsKey("Source")) {
            throw new RuntimeException(String.format("Cannot fetch the source file from S3: Invalid metadata received from the GoCD server. The artifact metadata must contain the key `%s`.", "source"));
        }
    }

    protected static class FetchArtifactRequest {
        @Expose
        @SerializedName("store_configuration")
        private ArtifactStoreConfig artifactStoreConfig;
        @Expose
        @SerializedName("artifact_metadata")
        private Map<String, String> metadata;

        public FetchArtifactRequest() {
        }

        public FetchArtifactRequest(ArtifactStoreConfig artifactStoreConfig, Map<String, String> metadata) {
            this.artifactStoreConfig = artifactStoreConfig;
            this.metadata = metadata;
        }

        public ArtifactStoreConfig getArtifactStoreConfig() {
            return artifactStoreConfig;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public static FetchArtifactRequest fromJSON(String json) {
            return Util.GSON.fromJson(json, FetchArtifactRequest.class);
        }
    }
}
