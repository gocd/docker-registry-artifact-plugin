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

import cd.go.artifact.docker.registry.executors.*;
import cd.go.artifact.docker.registry.utils.Util;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.annotation.Load;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.info.PluginContext;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Properties;

import static cd.go.artifact.docker.registry.Constants.PLUGIN_IDENTIFIER;

@Extension
public class S3ArtifactPlugin implements GoPlugin {
    public static final Logger LOG = Logger.getLoggerFor(S3ArtifactPlugin.class);
    private ConsoleLogger consoleLogger;

    @Load
    public void onLoad(PluginContext ctx) {
        final Properties properties = Util.getPluginProperties();
        LOG.info(String.format("Loading plugin %s[%s].", properties.getProperty("name"), properties.getProperty("pluginId")));
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        consoleLogger = ConsoleLogger.getLogger(accessor);
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        try {
            switch (Request.fromString(request.requestName())) {
                case REQUEST_GET_PLUGIN_ICON:
                    return new GetPluginIconExecutor().execute();
                case REQUEST_GET_PLUGIN_CAPABILITIES:
                    return new GetCapabilitiesExecutor().execute();
                case REQUEST_STORE_CONFIG_METADATA:
                    return new GetArtifactStoreConfigMetadataExecutor().execute();
                case REQUEST_STORE_CONFIG_VIEW:
                    return new GetArtifactStoreViewExecutor().execute();
                case REQUEST_STORE_CONFIG_VALIDATE:
                    return new ValidateArtifactStoreConfigExecutor(request).execute();
                case REQUEST_PUBLISH_ARTIFACT_METADATA:
                    return new GetPublishArtifactConfigMetadataExecutor().execute();
                case REQUEST_PUBLISH_ARTIFACT_VIEW:
                    return new GetPublishArtifactViewExecutor().execute();
                case REQUEST_PUBLISH_ARTIFACT_VALIDATE:
                    return new ValidatePublishArtifactConfigExecutor(request).execute();
                case REQUEST_FETCH_ARTIFACT_METADATA:
                    return new GetFetchArtifactMetadataExecutor().execute();
                case REQUEST_FETCH_ARTIFACT_VIEW:
                    return new GetFetchArtifactViewExecutor().execute();
                case REQUEST_FETCH_ARTIFACT_VALIDATE:
                    return new ValidateFetchArtifactConfigExecutor().execute();
                case REQUEST_PUBLISH_ARTIFACT:
                    return new PublishArtifactExecutor(request, consoleLogger).execute();
                case REQUEST_FETCH_ARTIFACT:
                    return new FetchArtifactExecutor(request, consoleLogger).execute();
                default:
                    throw new UnhandledRequestTypeException(request.requestName());
            }
        } catch (Exception e) {
            LOG.error("Error while executing request " + request.requestName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return PLUGIN_IDENTIFIER;
    }
}
