package cd.go.artifact.docker.registry.model;

public class UnresolvedPropertyException extends Exception {

    private final String partiallyResolvedTag;

    public UnresolvedPropertyException(String partiallyResolvedTag, String propertyName) {
        super(String.format("Failed to resolve one or more variables in %s: %s", propertyName, partiallyResolvedTag));
        this.partiallyResolvedTag = partiallyResolvedTag;
    }

    public String getPartiallyResolvedTag() {
        return partiallyResolvedTag;
    }
}
