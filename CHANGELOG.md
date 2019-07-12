# Changelog

## 1.1.0-104

* [bc23ccd](https://github.com/gocd/docker-registry-artifact-plugin/commit/bc23ccd) - Allow support to read System environment variables while publishing artifact.


## 1.0.1-92

* [9029e26](https://github.com/gocd/docker-registry-artifact-plugin/commit/9029e26) - Make plugin support old extension version too.
* [3588fc6](https://github.com/gocd/docker-registry-artifact-plugin/commit/3588fc6) - Add support for Amazon ECR.

## 1.0.0-25 (Needs GoCD 18.11 or newer)

* [76f7682](https://github.com/gocd/docker-registry-artifact-plugin/commit/76f7682) - Sets an environment variable with the artifact image details in the plugin API response. Also adds a view element to the plugin config view to provide a prefix for the name of the environment variable being set, which is not required.
* [87d0977](https://github.com/gocd/docker-registry-artifact-plugin/commit/87d0977) - Implements SkipImagePulling flag on fetch artifact tasks

## 1.0.0 - 2018-07-20

Initial release of plugin
