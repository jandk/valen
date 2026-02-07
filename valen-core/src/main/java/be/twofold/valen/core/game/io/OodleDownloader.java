package be.twofold.valen.core.game.io;

import be.twofold.valen.core.util.*;
import org.slf4j.*;

import java.net.*;
import java.net.http.*;
import java.nio.file.*;

public final class OodleDownloader {
    private static final String OODLE_URL = "https://github.com/WorkingRobot/OodleUE/raw/refs/heads/main/Engine/Source/Programs/Shared/EpicGames.Oodle/Sdk/2.9.10/win/redist/oo2core_9_win64.dll";

    private static final Logger LOG = LoggerFactory.getLogger(OodleDownloader.class);

    private OodleDownloader() {
    }

    public static Path download() {
        if (OperatingSystem.current() != OperatingSystem.Windows) {
            throw new UnsupportedOperationException("Oodle is only supported on Windows for now");
        }

        var uri = URI.create(OODLE_URL);
        var fileName = Path.of(uri.getPath()).getFileName();
        if (!Files.exists(fileName)) {
            downloadFile(uri, fileName);
        }
        return fileName;
    }

    private static void downloadFile(URI uri, Path path) {
        var client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        try (client) {
            var request = HttpRequest.newBuilder(uri)
                .GET()
                .build();

            LOG.info("Downloading file from {}", uri);
            var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new Exception("HTTP error: " + response.statusCode());
            }

            LOG.info("Saving file to {}", path);
            Files.write(path, response.body());
        } catch (Exception e) {
            LOG.error("Failed to download {}", uri, e);
            throw new RuntimeException(e);
        }
    }
}
