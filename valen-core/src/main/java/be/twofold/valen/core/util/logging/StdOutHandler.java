package be.twofold.valen.core.util.logging;

import java.util.logging.ConsoleHandler;

public final class StdOutHandler extends ConsoleHandler {
    public StdOutHandler() {
        setOutputStream(System.out);
    }
}
