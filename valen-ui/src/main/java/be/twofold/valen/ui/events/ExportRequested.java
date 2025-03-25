package be.twofold.valen.ui.events;

public record ExportRequested(
    String path,
    boolean recursive
) {
}
