package be.twofold.valen.core.util;

import java.net.*;
import java.nio.file.*;

public final class OodleDownloader {
    private static final String OODLE_URL = "https://github.com/WorkingRobot/OodleUE/raw/refs/heads/main/Engine/Source/Programs/Shared/EpicGames.Oodle/Sdk/2.9.10/win/redist/oo2core_9_win64.dll";

    private OodleDownloader() {
    }

    public static Path download() {
        if (OperatingSystem.current() != OperatingSystem.Windows) {
            throw new UnsupportedOperationException("Oodle is only supported on Windows for now");
        }

        var uri = URI.create(OODLE_URL);
        var fileName = Path.of(uri.getPath()).getFileName();
        if (!Files.exists(fileName)) {
            HttpUtils.downloadFile(uri, fileName);
        }
        return fileName;
    }
}
