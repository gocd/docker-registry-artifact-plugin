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

import cd.go.artifact.docker.model.PublishArtifactResponse;
import io.fabric8.docker.dsl.EventListener;

import java.util.concurrent.CountDownLatch;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;

public class DockerEventListener implements EventListener {
    private final CountDownLatch pushDone = new CountDownLatch(1);
    private PublishArtifactResponse publishArtifactResponse;
    private String imageToPush;

    public DockerEventListener() {
    }

    public DockerEventListener(PublishArtifactResponse publishArtifactResponse, String imageToPush) {
        this.publishArtifactResponse = publishArtifactResponse;
        this.imageToPush = imageToPush;
    }

    @Override
    public void onSuccess(String message) {
        LOG.info("Success:" + message);
        publishArtifactResponse.addMetadata("docker-image", imageToPush);
        pushDone.countDown();
    }

    @Override
    public void onError(String messsage) {
        LOG.error("Failure:" + messsage);
        publishArtifactResponse.addError("Failure:" + messsage);
        pushDone.countDown();
    }

    @Override
    public void onError(Throwable t) {
        LOG.error("Failure: " + t);
        publishArtifactResponse.addError("Failure: " + t);
        pushDone.countDown();
    }

    @Override
    public void onEvent(String event) {
        System.out.println(event);
    }

    public void await() throws InterruptedException {
        pushDone.await();
    }
}
