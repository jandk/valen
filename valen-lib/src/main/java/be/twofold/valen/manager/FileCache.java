package be.twofold.valen.manager;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public final class FileCache {
    private final Map<Path, SeekableByteChannel> channels = new HashMap<>();

    public SeekableByteChannel open(Path path) {
        if (channels.containsKey(path)) {
            return channels.get(path);
        }

        try {
            var channel = Files.newByteChannel(path, StandardOpenOption.READ);
            channels.put(path, channel);
            return channel;
        } catch (IOException e) {
            System.err.println("Failed to open file: " + path);
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("resource")
    private void close(Path path) {
        var channel = channels.get(path);
        if (channel == null) {
            return;
        }

        try {
            channel.close();
            channels.remove(path);
        } catch (IOException e) {
            System.err.println("Failed to close file: " + path);
            throw new UncheckedIOException(e);
        }
    }
}
