package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.source1.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public class VpkArchive implements Provider {
    private final Path path;
    private final Path root;
    private final Map<AssetID, Asset> assets = new HashMap<>();
    private final Provider parent;

    public VpkArchive(Path path, Provider parent) throws IOException {
        this.path = path;
        this.parent = parent;
        this.root = path.getParent();

        try (var source = new ChannelDataSource(Files.newByteChannel(path))) {
            source.expectInt(0x55aa1234);
            short versionMj = source.readShort();
            short versionMn = source.readShort();
            source.skip(4);
            if (versionMj >= 2) {
                if (versionMn == 0) {
                    source.skip(4 * 4);
                } else if (versionMn == 3) {
                    source.skip(4);
                }
            }
            while (true) {
                String ext = source.readCString();
                if (ext.isEmpty()) {
                    break;
                }
                while (true) {
                    String directory = source.readCString();
                    if (directory.isEmpty()) {
                        break;
                    }
                    while (true) {
                        String name = source.readCString();
                        if (name.isEmpty()) {
                            break;
                        }
                        SourceAssetID id = new SourceAssetID(getName(), "%s/%s.%s".formatted(directory, name, ext));
                        source.skip(4);
                        short preloadSize = source.readShort();
                        short archiveId = source.readShort();
                        int offset = source.readInt();
                        int size = source.readInt();
                        Check.state(source.readShort() == -1);
                        Map<String, Object> properties = new HashMap<>(3);
                        properties.put("offset", offset);
                        properties.put("archiveId", archiveId);

                        if (preloadSize > 0) {
                            byte[] preload = source.readBytes(preloadSize);
                            properties.put("preload", preload);
                        }
                        assets.put(id, new Asset(id, id.identifyAssetType(), size + preloadSize, Collections.unmodifiableMap(properties)));
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        String filename = path.getFileName().toString();
        filename = filename.substring(0, filename.length() - 4);
        if (filename.endsWith("_dir")) {
            return filename.substring(0, filename.length() - 4);
        }
        return filename;
    }

    @Override
    public Provider getParent() {
        return parent != null ? parent.getParent() : this;
    }

    @Override
    public List<Asset> assets() {
        return List.copyOf(assets.values());
    }

    @Override
    public boolean exists(AssetID identifier) {
        return assets.containsKey(identifier);
    }

    @Override
    public Object loadAsset(AssetID identifier) throws IOException {
        final Asset asset = assets.get(identifier);
        if (identifier instanceof SourceAssetID sourceIdentifier) {
            var reader = getReaders().stream().filter(rdr -> rdr.canRead(asset)).findFirst().orElseThrow();
            ByteBuffer buffer = loadRawAsset(sourceIdentifier);
            return reader.read(getParent(), asset, ByteArrayDataSource.fromBuffer(buffer));
        }
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        Asset asset = assets.get(identifier);
        short archiveId = (short) asset.properties().get("archiveId");
        long offset = Integer.toUnsignedLong((int) asset.properties().get("offset"));
        byte[] preload = (byte[]) asset.properties().get("preload");
        ByteBuffer data = ByteBuffer.allocate(asset.size());
        if (preload != null) {
            data.put(preload);
        }
        if (archiveId == 0x7FFF) {
            try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
                channel.read(data, offset);
            }
        } else {
            var subPath = root.resolve("%s_%03d.vpk".formatted(getName(), archiveId));
            try (FileChannel channel = FileChannel.open(subPath)) {
                channel.read(data, offset);
            }
        }
        return data.flip();
    }
}
