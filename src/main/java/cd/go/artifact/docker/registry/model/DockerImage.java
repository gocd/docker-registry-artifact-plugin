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

import cd.go.artifact.docker.registry.utils.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class DockerImage {
    @Expose
    @SerializedName("image")
    private String image;

    @Expose
    @SerializedName("tag")
    private String tag;

    public DockerImage() {
    }

    public DockerImage(String image, String tag) {
        this.image = image;
        this.tag = tag;
    }

    public String getImage() {
        return image;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", image, tag);
    }

    public static DockerImage fromFile(File file) throws FileNotFoundException {
        return Util.GSON.fromJson(new FileReader(file), DockerImage.class);
    }
}
