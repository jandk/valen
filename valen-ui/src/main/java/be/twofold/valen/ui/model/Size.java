package be.twofold.valen.ui.model;

public record Size(
    long size
) implements Comparable<Size> {
    @Override
    public int compareTo(Size o) {
        return Long.compare(size, o.size);
    }

    @Override
    public String toString() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KiB", size / (1024.0));
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MiB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GiB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
