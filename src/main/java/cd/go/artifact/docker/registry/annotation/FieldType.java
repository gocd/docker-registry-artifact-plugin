/*
 * Copyright 2022 Thoughtworks, Inc.
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

public enum FieldType {
    STRING {
        @Override
        public String validate(String value) {
            return null;
        }
    },

    POSITIVE_DECIMAL {
        @Override
        public String validate(String value) {
            try {
                if (Long.parseLong(value) < 0) {
                    return "must be positive decimal";
                }
            } catch (Exception e) {
                return "must be positive decimal";
            }
            return null;
        }
    },

    NUMBER {
        @Override
        public String validate(String value) {
            try {
                if (Double.parseDouble(value) < 0) {
                    return "must be number";
                }
            } catch (Exception e) {
                return "must be number";
            }

            return null;
        }
    };

    public abstract String validate(String value);
}
