package be.twofold.valen.game.eternal.resource;

public record ResourceName(
    String name
) implements Comparable<ResourceName> {
    public String pathname() {
        return name.substring(0, slashIndex(propIndex()));
    }

    public String filename() {
        return name.substring(slashIndex(propIndex()));
    }

    public String filenameWithoutProperties() {
        int propIndex = propIndex();
        var slashIndex = slashIndex(propIndex);
        return name.substring(slashIndex > 0 ? slashIndex + 1 : 0, propIndex);
    }

    public String extension() {
        var file = filenameWithoutProperties();
        var index = file.lastIndexOf('.');
        return index < 0 ? "" : file.substring(index + 1);
    }

    private int propIndex() {
        var index = name.indexOf('$');
        return index == -1 ? name.length() : index;
    }

    private int slashIndex(int from) {
        return Math.max(name.lastIndexOf('/', from - 1), 0);
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
