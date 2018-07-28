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

import cd.go.artifact.docker.registry.annotation.FieldMetadata;
import cd.go.artifact.docker.registry.annotation.Validatable;
import cd.go.artifact.docker.registry.utils.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtifactStoreConfig implements Validatable {
    @Expose
    @SerializedName("S3Bucket")
    @FieldMetadata(key = "S3Bucket", required = true)
    private String s3bucket;

    @Expose
    @SerializedName("AWSAccessKey")
    @FieldMetadata(key = "AWSAccessKey", required = true)
    private String awsaccesskey;

    @Expose
    @SerializedName("AWSSecretAccessKey")
    @FieldMetadata(key = "AWSSecretAccessKey", required = true, secure = true)
    private String awssecretaccesskey;


    public ArtifactStoreConfig() {
    }

    public ArtifactStoreConfig(String s3bucket, String awsaccesskey, String awssecretaccesskey) {
        this.s3bucket = s3bucket;
        this.awsaccesskey = awsaccesskey;
        this.awssecretaccesskey = awssecretaccesskey;
    }

    public String getS3bucket() {
        return s3bucket;
    }

    public String getAwsaccesskey() {
        return awsaccesskey;
    }

    public String getAwssecretaccesskey() {
        return awssecretaccesskey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactStoreConfig)) return false;

        ArtifactStoreConfig that = (ArtifactStoreConfig) o;

        if (s3bucket != null ? !s3bucket.equals(that.s3bucket) : that.s3bucket != null) return false;
        if (awsaccesskey != null ? !awsaccesskey.equals(that.awsaccesskey) : that.awsaccesskey != null) return false;
        return awssecretaccesskey != null ? awssecretaccesskey.equals(that.awssecretaccesskey) : that.awssecretaccesskey == null;
    }

    @Override
    public int hashCode() {
        int result = s3bucket != null ? s3bucket.hashCode() : 0;
        result = 31 * result + (awsaccesskey != null ? awsaccesskey.hashCode() : 0);
        result = 31 * result + (awssecretaccesskey != null ? awssecretaccesskey.hashCode() : 0);
        return result;
    }

    public static ArtifactStoreConfig fromJSON(String json) {
        return Util.GSON.fromJson(json, ArtifactStoreConfig.class);
    }
}
