package be.twofold.valen.core.util.logging;

import java.util.logging.*;

public final class StdOutHandler extends ConsoleHandler {
    public StdOutHandler() {
        setOutputStream(System.out);
    }
}
