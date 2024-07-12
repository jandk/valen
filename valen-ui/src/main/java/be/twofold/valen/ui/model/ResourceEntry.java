package be.twofold.valen.ui.model;

public record ResourceEntry(
    String name,
    String type,
    Size compressed,
    Size uncompressed
) {
}
