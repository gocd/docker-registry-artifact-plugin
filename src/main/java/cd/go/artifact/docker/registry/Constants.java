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

package cd.go.artifact.docker.registry;

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;

import java.util.Collections;

import static java.util.Arrays.asList;

public interface Constants {
    // The type of this extension
    String EXTENSION_TYPE = "artifact";

    // The extension point API version that this plugin understands
    String OLDER_EXTENSION_API_VERSION = "1.0";
    String EXTENSION_API_VERSION = "2.0";
    String CONSOLE_LOG_PROCESSOR_API_VERSION = "1.0";

    // the identifier of this plugin
    GoPluginIdentifier PLUGIN_IDENTIFIER = new GoPluginIdentifier(EXTENSION_TYPE, asList(OLDER_EXTENSION_API_VERSION, EXTENSION_API_VERSION));

    // requests that the plugin makes to the server
    String REQUEST_SERVER_PREFIX = "go.processor";
    String REQUEST_SERVER_GET_PLUGIN_SETTINGS = REQUEST_SERVER_PREFIX + ".plugin-settings.get";
    String SEND_CONSOLE_LOG = "go.processor.artifact.console-log";
}
