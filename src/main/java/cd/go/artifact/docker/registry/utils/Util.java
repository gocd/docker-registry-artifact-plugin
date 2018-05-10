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

package cd.go.artifact.docker.registry.utils;

import cd.go.artifact.docker.registry.annotation.FieldMetadata;
import cd.go.artifact.docker.registry.annotation.FieldMetadataTypeAdapter;
import cd.go.artifact.docker.registry.model.ArtifactPlanConfig;
import cd.go.artifact.docker.registry.model.ArtifactPlanConfigTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Util {
    public static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .serializeNulls()
            .registerTypeAdapter(ArtifactPlanConfig.class, new ArtifactPlanConfigTypeAdapter())
            .registerTypeAdapter(FieldMetadata.class, new FieldMetadataTypeAdapter())
            .create();

    public static String readResource(String resourceFile) {
        return new String(readResourceBytes(resourceFile), StandardCharsets.UTF_8);
    }

    public static byte[] readResourceBytes(String resourceFile) {
        try (InputStream is = Util.class.getResourceAsStream(resourceFile)) {
            return readFully(is);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }

    private static byte[] readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public static String pluginId() {
        return getPluginProperties().getProperty("pluginId");
    }

    public static Properties getPluginProperties() {
        String propertiesAsAString = readResource("/plugin.properties");
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(propertiesAsAString));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}

