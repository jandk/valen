package be.twofold.valen.ui.service;

public record ExportProgress(
    int done,
    int total,
    String message
) {
}
