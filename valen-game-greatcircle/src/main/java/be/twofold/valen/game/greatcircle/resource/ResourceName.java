package be.twofold.valen.game.greatcircle.resource;

public record ResourceName(
    String name
) implements Comparable<ResourceName> {
    public String pathname() {
        return name.substring(0, Math.max(slashIndex(propIndex()), 0));
    }

    public String filename() {
        return name.substring(slashIndex(propIndex()) + 1);
    }

    public String filenameWithoutProperties() {
        int propIndex = propIndex();
        var slashIndex = slashIndex(propIndex);
        return name.substring(slashIndex + 1, propIndex);
    }

    private int propIndex() {
        var index = name.indexOf('$');
        return index == -1 ? name.length() : index;
    }

    private int slashIndex(int from) {
        return name.lastIndexOf('/', from - 1);
    }

    @Override
    public int compareTo(ResourceName o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
