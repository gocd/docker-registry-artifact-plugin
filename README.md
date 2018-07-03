# Docker Registry Artifact plugin

The docker registry artifact plugin provides a way to push and pull docker images on GoCD 

## Building the code base

To build the jar, run `./gradlew clean test assemble`

## Getting started

### Requirements

- GoCD Server 18.7+
- Docker registry
- Docker daemon

### Installation

Build the plugin with `./gradlew clean test assemble`

Copy the file build/libs/docker-registry-artifact-plugin-VERSION.jar to the GoCD server under ${GO_SERVER_DIR}/plugins/external and restart the server. The GO_SERVER_DIR is usually /var/lib/go-server on Linux and C:\Program Files\Go Server on Windows.

### Configuration

There are three levels of configuration needed in order for publishing and fetching a docker image as an artifact.

#### Configure the Artifact Store

1. Login to GoCD server as admin and navigate to Admin > Artifact Stores
2. Click on `Add` to add the store details for the plugin.
   - Specify the store id
   - Choose the docker registry artifact plugin
   - Specify the `Docker Registry Url`
   - Specify `username` and `password`

   ![](images/artifact_store.png)

#### Publish artifact config

1. Navigate to the `Artifacts` tab under Stage > Job
2. Choose artifact type `External`
   - Specify the `artifact id`.
   - Specify the `store id` of an existing artifact store
3. Configure the build specific artifact details.
   - Optionally specify the `Image` and `Tag`. Usually, the `Tag` can be an environment variable that changes for every build. For example, the `GO_PIPELINE_LABEL` can be used to publish a versioned docker image.
   - Optionally specify the `BuildFile`. Specify a json file name. The format of the file should be `{"image": "name", "tag": "tag pattern"}`. The `BuildFile` option can be used if the `Tag` option cannot be specified for the tag pattern you want to use. The generation of this json file must be handled within the GoCD job.

   ![](images/build_and_publish_image_artifacts.png)

#### Fetch artifact config

To pull an image from an upstream pipeline or stage, the following information is required:

1. Specify a `Pipeline Dependency` material if you have an upstream pipeline you want to pull the docker image.
2. Navigate to `Tasks` tab under Stage > Job
3. Add a `Fetch Artifact` task
   -  Choose artifact origin type `External`
   - Specify the pipeline, stage and job name.
   - Specify the artifact id of the external artifact that belongs to the upstream job

## License

```plain
Copyright 2018 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## About the license and releasing your plugin under a different license

The code in this repository is licensed under the Apache 2.0 license. The license itself specifies the terms
under which derivative works may be distributed (the license also defines derivative works). The Apache 2.0 license is a
permissive open source license that has minimal requirements for downstream licensors/licensees to comply with.

This does not prevent your plugin from being licensed under a different license as long as you comply with the relevant
clauses of the Apache 2.0 license (especially section 4). Typically, you clone this repository and keep the existing
copyright notices. You are free to add your own license and copyright notice to any modifications.

This is not legal advice. Please contact your lawyers if needed.
