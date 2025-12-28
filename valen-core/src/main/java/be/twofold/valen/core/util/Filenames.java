package be.twofold.valen.core.util;

public final class Filenames {
    private Filenames() {
    }

    public static String getPath(String filename) {
        int index = filename.lastIndexOf('/');
        return index < 0 ? "" : filename.substring(0, index);
    }

    public static String getName(String filename) {
        return filename.substring(filename.lastIndexOf('/') + 1);
    }

    public static String getBaseName(String filename) {
        return removeExtension(getName(filename));
    }

    public static String getExtension(String filename) {
        int index = indexOfExtension(filename);
        return index < 0 ? "" : filename.substring(index + 1);
    }

    public static String removeExtension(String filename) {
        int index = indexOfExtension(filename);
        return index < 0 ? filename : filename.substring(0, index);
    }

    public static int indexOfExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        int slashIndex = filename.lastIndexOf('/');
        return slashIndex > dotIndex ? -1 : dotIndex;
    }
}
