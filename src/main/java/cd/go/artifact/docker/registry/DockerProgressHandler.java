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

package cd.go.artifact.docker.registry;

import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.ProgressMessage;
import org.apache.commons.lang.StringUtils;

import static cd.go.artifact.docker.registry.DockerRegistryArtifactPlugin.LOG;
import static java.lang.String.format;

public class DockerProgressHandler implements ProgressHandler {
    private final ConsoleLogger consoleLogger;
    private String digest;

    public DockerProgressHandler(ConsoleLogger consoleLogger) {
        this.consoleLogger = consoleLogger;
    }

    @Override
    public void progress(ProgressMessage message) {
        if (StringUtils.isNotBlank(message.error())) {
            consoleLogger.error(message.error());
            LOG.error(format("Failure: %s", message.error()));
            throw new RuntimeException(message.error());
        }

        if (StringUtils.isNotBlank(message.progress())) {
            consoleLogger.info(message.progress());
        }

        if (StringUtils.isNotBlank(message.digest())) {
            digest = message.digest();
        }
    }

    public String getDigest() {
        return digest;
    }
}
