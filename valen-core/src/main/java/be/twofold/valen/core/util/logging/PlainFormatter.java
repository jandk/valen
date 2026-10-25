package be.twofold.valen.core.util.logging;

public final class PlainFormatter extends AbstractFormatter {
    @Override
    String levelColor(String level) {
        return "";
    }

    @Override
    String loggerColor() {
        return "";
    }

    @Override
    String reset() {
        return "";
    }
}
