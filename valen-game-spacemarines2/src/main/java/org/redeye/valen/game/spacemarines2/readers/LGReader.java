package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.converters.*;
import org.redeye.valen.game.spacemarines2.serializers.scene.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.scene.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public class LGReader implements Reader<Model> {
    private GeometryManagerToModel converter = new GeometryManagerToModel();

    @Override
    public Model read(Archive archive, Asset asset, DataSource source) throws IOException {
        if (!(asset.id() instanceof EmperorAssetId lgId)) {
            return null;
        }
        ResourceHeader ignored = ResourceHeader.read(source);
        ScnSceneSerializer serializer = new ScnSceneSerializer();
        ScnScene scene = serializer.load(source);
        var resourceId = lgId.withExtension(".scn.resource");

        ObjGeomVBufferMapping vBufferMapping = scene.getGeomManager().vBufferMapping;
        List<ObjGeomVBufferInfo> vBufferInfo = vBufferMapping.vBufferInfo;
        List<ByteBuffer> vStreams = new ArrayList<>(vBufferInfo.size());

        if (archive.exists(lgId.withExtension(".lg_data"))) {
            var streamDataTmp = ByteBuffer.wrap(archive.loadAsset(lgId.withExtension(".lg_data"), byte[].class));
            streamDataTmp.position(16);
            for (int i = 0; i < vBufferInfo.size(); i++) {
                ObjGeomVBufferInfo objGeomVBufferInfo = vBufferInfo.get(i);
                var bufData = new byte[objGeomVBufferInfo.size];
                streamDataTmp.get(bufData);
                vStreams.add(ByteBuffer.wrap(bufData));
            }

            List<ObjGeomStream> streams = scene.getGeomManager().streams;
            for (int i = 0; i < streams.size(); i++) {
                ObjGeomStream stream = streams.get(i);
                ObjGeomStreamToVBuffer objGeomStreamToVBuffer = vBufferMapping.streamToVBuffer.get(i);
                var buffer = vStreams.get(objGeomStreamToVBuffer.vBufIdx);
                buffer.position(objGeomStreamToVBuffer.vBufOffset);
                Check.state((stream.state & 2) == 0);
                stream.data = new byte[Math.toIntExact(stream.size)];
                buffer.get(stream.data);
            }
        }

        return converter.convert(archive, lgId, resourceId, scene.getGeomManager(), null);
    }


    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.fileName().endsWith(".lg");
        }
        return false;
    }
}
