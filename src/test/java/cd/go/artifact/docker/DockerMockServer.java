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

import io.fabric8.docker.api.model.AuthConfig;
import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.mockwebserver.DefaultMockServer;
import io.fabric8.mockwebserver.ServerRequest;
import io.fabric8.mockwebserver.ServerResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;

public class DockerMockServer extends DefaultMockServer {
    public DockerMockServer() {
        this(true);
    }

    public DockerMockServer(boolean useHttps) {
        super(useHttps);
    }

    public DockerMockServer(MockWebServer server, Map<ServerRequest, Queue<ServerResponse>> responses, boolean useHttps) {
        super(server, responses, useHttps);
    }

    public void init() {
        start();
    }

    public void destroy() {
        shutdown();
    }

    public DockerClient createClient() throws InterruptedException {
        final AuthConfig authConfig = new AuthConfig(null, null, "admin", "localhost:5000", "admin");
        Config config = new ConfigBuilder()
                .withDockerUrl(url("/"))
                .withAuthConfigs(Collections.singletonMap("localhost:5000", authConfig))
                .build();

        return new DefaultDockerClient(config);
    }
}
