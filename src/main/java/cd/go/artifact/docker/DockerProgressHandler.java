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

package cd.go.artifact.docker;

import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.ProgressMessage;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;
import static java.lang.String.format;

public class DockerProgressHandler implements ProgressHandler {
    private final ConsoleLogger consoleLogger;
    private List<String> errors = new ArrayList<>();
    private String digest;

    public DockerProgressHandler(ConsoleLogger consoleLogger) {
        this.consoleLogger = consoleLogger;
    }

    @Override
    public void progress(ProgressMessage message) {
        if (StringUtils.isNotBlank(message.error())) {
            consoleLogger.error(message.error());
            LOG.error(format("Failure: %s", message.error()));
            errors.add(format("Failure: %s", message.error()));
            return;
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

    public List<String> getErrors() {
        return errors;
    }
}
