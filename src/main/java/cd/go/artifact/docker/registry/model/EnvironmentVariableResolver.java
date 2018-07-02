package cd.go.artifact.docker.registry.model;

import org.apache.commons.lang.text.StrSubstitutor;

import java.util.Map;
import java.util.regex.Pattern;

public class EnvironmentVariableResolver {

    private static final Pattern ENVIRONMENT_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*)\\}");
    private final String property;
    private String propertyName;

    public EnvironmentVariableResolver(String property, String propertyName) {
        this.property = property;
        this.propertyName = propertyName;
    }

    public String resolve(Map<String, String> environmentVariables) throws UnresolvedPropertyException {
        String evaluatedProperty = StrSubstitutor.replace(property, environmentVariables);
        if(ENVIRONMENT_VARIABLE_PATTERN.matcher(evaluatedProperty).find()) {
            throw new UnresolvedPropertyException(evaluatedProperty, propertyName);
        }
        return evaluatedProperty;
    }
}
