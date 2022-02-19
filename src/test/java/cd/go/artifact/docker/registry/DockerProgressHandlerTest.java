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

import com.spotify.docker.client.messages.ProgressMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class DockerProgressHandlerTest {
    private ConsoleLogger consoleLogger;
    private DockerProgressHandler progressHandler;

    @BeforeEach
    public void setUp() {
        consoleLogger = mock(ConsoleLogger.class);
        progressHandler = new DockerProgressHandler(consoleLogger);
    }

    @Test
    public void shouldLogErrorToConsoleLogger() {
        assertThatThrownBy(() -> progressHandler.progress(ProgressMessage.builder().error("some-error").build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("some-error");

        verify(consoleLogger, times(1)).error("some-error");
    }

    @Test
    public void shouldLogProgressToConsoleLogger() {
        progressHandler.progress(ProgressMessage.builder().progress("docker-push-pull-progress").build());

        verify(consoleLogger, times(1)).info("docker-push-pull-progress");
    }
}