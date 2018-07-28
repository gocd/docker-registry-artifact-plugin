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

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;

import static cd.go.artifact.docker.registry.Constants.API_VERSION;
import static cd.go.artifact.docker.registry.Constants.PLUGIN_IDENTIFIER;
import static cd.go.artifact.docker.registry.S3ArtifactPlugin.LOG;

public class ConsoleLogger {
    private static ConsoleLogger consoleLogger;
    private final GoApplicationAccessor accessor;

    private ConsoleLogger(GoApplicationAccessor accessor) {
        this.accessor = accessor;
    }

    public void info(String message) {
        sendLog(new ConsoleLogMessage(ConsoleLogMessage.LogLevel.INFO, message));
    }

    public void error(String message) {
        sendLog(new ConsoleLogMessage(ConsoleLogMessage.LogLevel.ERROR, message));
    }

    private void sendLog(ConsoleLogMessage consoleLogMessage) {
        DefaultGoApiRequest request = new DefaultGoApiRequest(Constants.SEND_CONSOLE_LOG, API_VERSION, PLUGIN_IDENTIFIER);
        request.setRequestBody(consoleLogMessage.toJSON());

        GoApiResponse response = accessor.submit(request);
        if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
            LOG.error(String.format("Failed to submit console log: %s", response.responseBody()));
        }
    }

    public static ConsoleLogger getLogger(GoApplicationAccessor accessor) {
        if (consoleLogger == null) {
            synchronized (ConsoleLogger.class) {
                if (consoleLogger == null) {
                    consoleLogger = new ConsoleLogger(accessor);
                }
            }
        }

        return consoleLogger;
    }

    static class ConsoleLogMessage {
        private LogLevel logLevel;
        private String message;

        public ConsoleLogMessage(LogLevel logLevel, String message) {
            this.message = message;
            this.logLevel = logLevel;
        }

        public String toJSON() {
            return new Gson().toJson(this);
        }

        enum LogLevel {
            INFO, ERROR
        }
    }
}
