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

package cd.go.artifact.docker.registry.annotation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConfigMetadata {

    @Expose
    @SerializedName("key")
    private String key;

    @Expose
    @SerializedName("metadata")
    private FieldMetadata metadata;

    public ConfigMetadata(String key, FieldMetadata metadata) {
        this.key = key;
        this.metadata = metadata;
    }

    public String getKey() {
        return key;
    }

    public boolean isRequired() {
        return metadata.required();
    }
}
