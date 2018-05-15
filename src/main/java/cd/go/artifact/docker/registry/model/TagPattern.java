package cd.go.artifact.docker.registry.model;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.Map;
import java.util.regex.Pattern;

public class TagPattern {

    private static final Pattern ENVIRONMENT_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*)\\}");
    private final String tagPattern;

    public TagPattern(String tagPattern) {
        this.tagPattern = tagPattern;
    }

    public String resolve(Map<String, String> environmentVariables) throws UnresolvedTagException {
        String tag = StrSubstitutor.replace(tagPattern, environmentVariables);
        if(ENVIRONMENT_VARIABLE_PATTERN.matcher(tag).find()) {
            throw new UnresolvedTagException(tag);
        }
        return tag;
    }
}
