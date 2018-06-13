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

package cd.go.artifact.docker.registry.executors;

import cd.go.artifact.docker.registry.annotation.ConfigMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class ViewTest {
    @Test
    public void allFieldsShouldBePresentInView() throws Exception {
        final Document document = getDocument();
        final List<ConfigMetadata> metadataList = getMetadataList();
        for (ConfigMetadata field : metadataList) {
            final String name = field.getKey();
            final Elements inputFieldForName = document.getElementsByAttributeValue("ng-model", name);
            assertThat(inputFieldForName).describedAs(format("No input/textarea defined for {0}", name)).hasSize(1);
            assertThat(inputFieldForName.get(0).attr("ng-class"))
                    .describedAs(format("ng-class attribute is not defined on {0}", inputFieldForName))
                    .isEqualTo(format("'{'''is-invalid-input'': GOINPUTNAME[{0}].$error.server'}'", name));

            final Elements spanToShowError = document.getElementsByAttributeValue("ng-class", format("'{'''is-visible'': GOINPUTNAME[{0}].$error.server'}'", name));
            assertThat(spanToShowError).hasSize(1);
            assertThat(spanToShowError.attr("ng-show")).isEqualTo(format("GOINPUTNAME[{0}].$error.server", name));
            assertThat(spanToShowError.text()).isEqualTo(format("'{{'GOINPUTNAME[{0}].$error.server'}}'", name));
        }

        final Elements inputs = document.select("textarea, input, select");
        assertThat(inputs).describedAs("should contains only inputs that defined in ElasticProfile.java").hasSize(metadataList.size());
    }


    private Document getDocument() throws Exception {
        final GoPluginApiResponse response = getRequestExecutor().execute();
        final Map<String, String> responseHash = new Gson().fromJson(response.responseBody(), new TypeToken<Map<String,String>>(){}.getType());

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(responseHash).containsKey("template");

        return Jsoup.parse(responseHash.get("template"));
    }

    protected abstract List<ConfigMetadata> getMetadataList();

    protected abstract RequestExecutor getRequestExecutor();
}
