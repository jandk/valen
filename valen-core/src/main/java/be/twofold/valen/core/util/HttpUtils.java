package be.twofold.valen.core.util;

import org.slf4j.*;

import java.net.*;
import java.net.http.*;
import java.nio.file.*;

public final class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private HttpUtils() {
    }

    public static void downloadFile(URI uri, Path path) {
        var client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        try (client) {
            var request = HttpRequest.newBuilder(uri)
                .GET()
                .build();

            log.info("Downloading file from {}", uri);
            var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new Exception("HTTP error: " + response.statusCode());
            }

            log.info("Saving file to {}", path);
            Files.write(path, response.body());
        } catch (Exception e) {
            log.error("Failed to download {}", uri, e);
            throw new RuntimeException(e);
        }
    }
}
