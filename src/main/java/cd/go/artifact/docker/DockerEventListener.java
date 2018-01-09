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

import io.fabric8.docker.dsl.EventListener;

import java.util.concurrent.CountDownLatch;

public class DockerEventListener implements EventListener {
    private final CountDownLatch pushDone = new CountDownLatch(1);

    @Override
    public void onSuccess(String message) {
        System.out.println("Success:" + message);
        pushDone.countDown();
    }

    @Override
    public void onError(String messsage) {
        System.err.println("Failure:" + messsage);
        pushDone.countDown();
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace(System.err);
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
