package be.twofold.valen.core.util.logging;

public final class ColoredFormatter extends AbstractFormatter {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    @Override
    String levelColor(String level) {
        return switch (level) {
            case "SEVERE" -> RED;
            case "WARNING" -> YELLOW;
            case "INFO", "FINE" -> GREEN;
            default -> RESET;
        };
    }

    @Override
    String loggerColor() {
        return CYAN;
    }

    @Override
    String reset() {
        return RESET;
    }
}
