package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import org.slf4j.*;

import java.net.*;
import java.net.http.*;
import java.nio.file.*;

public final class OodleDownloader {
    private static final Logger log = LoggerFactory.getLogger(OodleDownloader.class);

    private static final String PREFIX = "https://github.com/WorkingRobot/OodleUE/raw/refs/heads/main/Engine/Source/Programs/Shared/EpicGames.Oodle/Sdk/2.9.10/";
    private static final String OODLE_WINDOWS_X86 = PREFIX + "win/redist/oo2core_9_win32.dll";
    private static final String OODLE_WINDOWS_X86_64 = PREFIX + "win/redist/oo2core_9_win64.dll";
    private static final String OODLE_LINUX_X86_64 = PREFIX + "linux/lib/liboo2corelinux64.so.9";
    private static final String OODLE_LINUX_ARM64 = PREFIX + "linuxarm/lib/liboo2corelinuxarm64.so.9";
    private static final String OODLE_LINUX_ARM32 = PREFIX + "linuxarm/lib/liboo2corelinuxarm32.so.9";
    private static final String OODLE_MACOSX = PREFIX + "mac/lib/liboo2coremac64.2.9.10.dylib";

    private OodleDownloader() {
    }

    public static Path download() {
        var platform = Platform.current();
        var uriString = switch (platform.os()) {
            case WINDOWS -> switch (platform.arch()) {
                case X86_64 -> OODLE_WINDOWS_X86_64;
                case X86 -> OODLE_WINDOWS_X86;
                default ->
                    throw new UnsupportedOperationException("Unsupported architecture on Windows: " + platform.arch());
            };
            case LINUX -> switch (platform.arch()) {
                case X86_64 -> OODLE_LINUX_X86_64;
                case ARM_64 -> OODLE_LINUX_ARM64;
                case ARM -> OODLE_LINUX_ARM32;
                default ->
                    throw new UnsupportedOperationException("Unsupported architecture on Linux: " + platform.arch());
            };
            case MAC -> OODLE_MACOSX;
        };

        var uri = URI.create(uriString);
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

            log.info("Downloading Oodle from {}", uri);
            var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new Exception("HTTP error: " + response.statusCode());
            }

            log.info("Saving to {}", path);
            Files.write(path, response.body());
        } catch (Exception e) {
            log.error("Failed to download {}", uri, e);
            throw new RuntimeException(e);
        }
    }
}
