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
import cd.go.artifact.docker.registry.annotation.ValidationResult;
import cd.go.artifact.docker.registry.utils.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

public class ArtifactStoreConfig implements Validatable {
    @Expose
    @SerializedName("RegistryURL")
    @FieldMetadata(key = "RegistryURL", required = true)
    private String registryUrl;

    @Expose
    @SerializedName("RegistryType")
    @FieldMetadata(key = "RegistryType", required = true)
    private String registryType;

    @Expose
    @SerializedName("AWSAccessKeyId")
    @FieldMetadata(key = "AWSAccessKeyId", required = false, secure = true)
    private String awsAccessKeyId;

    @Expose
    @SerializedName("AWSSecretAccessKey")
    @FieldMetadata(key = "AWSSecretAccessKey", required = false, secure = true)
    private String awsSecretAccessKey;

    @Expose
    @SerializedName("AWSRegion")
    @FieldMetadata(key = "AWSRegion", required = false, secure = false)
    private String awsRegion;

    @Expose
    @SerializedName("Username")
    @FieldMetadata(key = "Username", required = false)
    private String username;

    @Expose
    @SerializedName("Password")
    @FieldMetadata(key = "Password", required = false, secure = true)
    private String password;

    public ArtifactStoreConfig() {
    }

    public ArtifactStoreConfig(String registryUrl, String registryType) {
        this.registryUrl = registryUrl;
        this.registryType = registryType;
    }

    public ArtifactStoreConfig(String registryUrl, String registryType, String username, String password) {
        this(registryUrl, registryType);
        this.username = username;
        this.password = password;
    }

    public ArtifactStoreConfig(String registryUrl, String registryType, String awsAccessKeyId, String awsSecretAccessKey, String awsRegion) {
        this(registryUrl, registryType);
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretAccessKey = awsSecretAccessKey;
        this.awsRegion = awsRegion;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRegistryType() {
        return registryType;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public boolean isRegistryTypeEcr() {
        return "ecr".equals(registryType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtifactStoreConfig)) return false;

        ArtifactStoreConfig that = (ArtifactStoreConfig) o;

        if (registryUrl != null ? !registryUrl.equals(that.registryUrl) : that.registryUrl != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (registryType != null ? !registryType.equals(that.registryType) : that.registryType != null) return false;
        if (awsAccessKeyId != null ? !awsAccessKeyId.equals(that.awsAccessKeyId) : that.awsAccessKeyId != null) return false;
        if (awsSecretAccessKey != null ? !awsSecretAccessKey.equals(that.awsSecretAccessKey) : that.awsSecretAccessKey != null) return false;
        if (awsRegion != null ? !awsRegion.equals(that.awsRegion) : that.awsRegion != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = registryUrl != null ? registryUrl.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (registryType != null ? registryType.hashCode() : 0);
        result = 31 * result + (awsAccessKeyId != null ? awsAccessKeyId.hashCode() : 0);
        result = 31 * result + (awsSecretAccessKey != null ? awsSecretAccessKey.hashCode() : 0);
        result = 31 * result + (awsRegion != null ? awsRegion.hashCode() : 0);
        return result;
    }

    public static ArtifactStoreConfig fromJSON(String json) {
        return Util.GSON.fromJson(json, ArtifactStoreConfig.class);
    }


    @Override
    public ValidationResult validate() {
        ValidationResult validationResult = new ValidationResult();
        if (StringUtils.isBlank(registryUrl)) {
            validationResult.addError("RegistryURL", "RegistryURL must not be blank.");
        }
        if (StringUtils.isBlank(registryType)) {
            validationResult.addError("RegistryType", "RegistryType must not be blank.");
        }
        else if (!"other".equals(registryType) && !"ecr".equals(registryType)) {
            validationResult.addError("RegistryType", "RegistryType must either be `ecr` or `other`.");
        }
        if ("other".equals(registryType)) {
            if(StringUtils.isBlank(username)) {
                validationResult.addError("Username", "Username must not be blank.");
            }
            if(StringUtils.isBlank(password)) {
                validationResult.addError("Password", "Password must not be blank.");
            }
        }
        if ("ecr".equals(registryType)) {
            if(StringUtils.isBlank(awsAccessKeyId)) {
                validationResult.addError("AWSAccessKeyId", "AWSAccessKeyId must not be blank.");
            }
            if(StringUtils.isBlank(awsSecretAccessKey)) {
                validationResult.addError("AWSSecretAccessKey", "AWSSecretAccessKey must not be blank.");
            }
            if(StringUtils.isBlank(awsRegion)) {
                validationResult.addError("AWSRegion", "AWSRegion must not be blank.");
            }
        }
        return validationResult;
    }
}
