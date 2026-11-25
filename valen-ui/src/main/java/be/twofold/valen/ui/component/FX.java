package be.twofold.valen.ui.component;

import javafx.application.*;

import java.util.concurrent.*;
import java.util.function.*;

public final class FX {
    private FX() {
    }

    public static void run(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public static <T> CompletableFuture<T> supply(Supplier<T> action) {
        var future = new CompletableFuture<T>();
        run(() -> {
            try {
                future.complete(action.get());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
