package cd.go.artifact.docker.registry.model;

public class UnresolvedTagException extends Exception {

    private final String partiallyResolvedTag;

    public UnresolvedTagException(String partiallyResolvedTag) {
        super("Failed to resolve one or more variables in tag: " + partiallyResolvedTag);
        this.partiallyResolvedTag = partiallyResolvedTag;
    }

    public String getPartiallyResolvedTag() {
        return partiallyResolvedTag;
    }
}
