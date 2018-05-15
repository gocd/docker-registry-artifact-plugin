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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MetadataHelper {

    public static List<ConfigMetadata> getMetadata(Class<?> clazz) {
        return buildMetadata(clazz);
    }

    private static List<ConfigMetadata> buildMetadata(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<ConfigMetadata> metadata = new ArrayList<>();
        for (Field field : fields) {
            FieldMetadata profileField = field.getAnnotation(FieldMetadata.class);
            if (profileField != null) {
                final ConfigMetadata configMetadata = new ConfigMetadata(profileField.key(), profileField);
                metadata.add(configMetadata);
            }
        }
        return metadata;
    }
}
