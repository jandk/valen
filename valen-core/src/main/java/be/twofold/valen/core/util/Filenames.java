package be.twofold.valen.core.util;

public final class Filenames {
    private Filenames() {
    }

    public static String fileName(String path) {
        var index = path.lastIndexOf('/');
        return index < 0 ? path : path.substring(index + 1);
    }

    public static String fileNameWithoutExtension(String path) {
        return removeExtension(fileName(path));
    }

    public static String pathName(String path) {
        var index = path.lastIndexOf('/');
        return index < 0 ? "" : path.substring(0, index);
    }

    public static String removeExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index < 0 ? fileName : fileName.substring(0, index);
    }
}
