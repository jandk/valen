package be.twofold.valen.ui.service;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public interface Dialogs {

    CompletableFuture<Optional<Path>> chooseGameExecutable();

    CompletableFuture<Optional<Path>> chooseSaveFile(String initialFilename);

    void showError(String header, String message);

    void showException(String message, Throwable throwable);

}
