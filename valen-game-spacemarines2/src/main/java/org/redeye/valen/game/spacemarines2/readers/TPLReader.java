package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.redeye.valen.game.spacemarines2.converters.*;
import org.redeye.valen.game.spacemarines2.serializers.template.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;

public class TPLReader implements Reader<Model> {
    private GeometryManagerToModel converter = new GeometryManagerToModel();

    @Override
    public Model read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        if (!(asset.id() instanceof EmperorAssetId tplId)) {
            return null;
        }
        ResourceHeader ignored = ResourceHeader.read(source);
        AnimTplSerializer serializer = new AnimTplSerializer();
        AnimTemplate animTemplate = serializer.load(source);

        if (animTemplate.geometryManager == null) {
            return null;
        }

        var resourceId = tplId.withExtension(".tpl.resource");

        ByteBuffer streamData = null;
        if (archive.exists(tplId.withExtension(".tpl_data"))) {
            streamData = ByteBuffer.wrap(archive.loadAsset(tplId.withExtension(".tpl_data"), byte[].class));
        }

        var geometryManager = animTemplate.geometryManager;
        var streams = geometryManager.streams;
        Files.createDirectories(Path.of("streams"));
        if (geometryManager.geomSetsInfo != null) {
            for (int streamId = 0; streamId < geometryManager.geomSetsInfo.getStreamRefs().size(); streamId++) {
                var streamRef = geometryManager.geomSetsInfo.getStreamRefs().get(streamId);
                var stream = streams.get(streamId);
                if ((stream.state & 2) == 0) {
                    if (streamData == null) {
                        throw new IllegalStateException("No stream found for external source stream(%s).".formatted(streamId));
                    }
                    Check.state(streamRef.getSize() == stream.size);
                    stream.data = new byte[Math.toIntExact(streamRef.getSize())];
                    streamData.get(Math.toIntExact(streamRef.getOffset()), stream.data, 0, Math.toIntExact(streamRef.getSize()));
                    Files.write(Path.of("streams/stream_" + streamId + ".bin"), stream.data);
                }
            }
        }
        return converter.convert(archive, tplId, resourceId, animTemplate.geometryManager, animTemplate.lodDef);
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.inferAssetType() == AssetType.MODEL && id.fileName().endsWith(".tpl");
        }
        return false;
    }
}
