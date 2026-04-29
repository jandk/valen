package be.twofold.valen.ui.component.progress;

public record Progress(
    String message,
    double progress,
    double workDone,
    double totalWork
) {
}
