package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.psSection.*;
import org.redeye.valen.game.spacemarines2.serializers.template.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;
import java.util.*;

public final class GeometryManager {

    public List<ObjLodRoot> lodRoots;
    public ObjGeomSetsInfo geomSetsInfo;
    public Object debugInfo;
    public List<ObjObj> objects;
    public List<ObjSplitRange> objSplitInfo;
    public List<Matrix4> mtrLt;
    public List<Matrix4> mtrModel;
    public List<Short> namedObjectsId;
    public List<String> namedObjectsName;
    public List<ObjSplit> splits;
    public List<ObjGeom> geoms;
    public List<ObjGeomStream> streams;
    public List<Pair<Integer, ObjPropStorage>> objectProps;
    public List<Pair<Integer, String>> objPs;
    public Short rootObjId;
    public ObjGeomVBufferMapping vBufferMapping;
    public GeometryManagerState state = GeometryManagerState.STREAMING_UNLOADED;
    private GeometryManagerInfo info;

    public GeometryManager() {

    }

    public void setvBufferMapping(ObjGeomVBufferMapping vBufferMapping) {
        this.vBufferMapping = vBufferMapping;
    }

    public void setObjPs(List<Pair<Integer, String>> objPs) {
        this.objPs = objPs;
    }

    public void setLodRoots(List<ObjLodRoot> lodRoots) {
        this.lodRoots = lodRoots;
    }

    public void setGeomSetsInfo(ObjGeomSetsInfo geomSetsInfo) {
        this.geomSetsInfo = geomSetsInfo;
    }

    public void setDebugInfo(Object debugInfo) {
        this.debugInfo = debugInfo;
    }

    public void setObjects(List<ObjObj> object) {
        this.objects = object;
    }

    public void setObjectProps(List<Pair<Integer, ObjPropStorage>> objectProps) {
        this.objectProps = objectProps;
    }

    public void setObjSplitInfo(List<ObjSplitRange> objSplitInfo) {
        this.objSplitInfo = objSplitInfo;
    }

    public void setMtrLt(List<Matrix4> mtrLt) {
        this.mtrLt = mtrLt;
    }

    public void setMtrModel(List<Matrix4> mtrModel) {
        this.mtrModel = mtrModel;
    }

    public void setNamedObjectsId(List<Short> namedObjectsId) {
        this.namedObjectsId = namedObjectsId;
    }

    public void setNamedObjectsName(List<String> namedObjectsName) {
        this.namedObjectsName = namedObjectsName;
    }

    public void setSplits(List<ObjSplit> splits) {
        this.splits = splits;
    }

    public void onReadFinishCallback(DataSource source, FioStructSerializer<GeometryManager> serializer) throws IOException {

        if (objSplitInfo != null) {
            Check.argument(objects.size() == objSplitInfo.size());
        }
        if (mtrLt != null) {
            Check.argument(objects.size() == mtrLt.size());
        }
        if (mtrModel != null) {
            Check.argument(objects.size() == mtrModel.size());
        }

        for (int i = 0; i < objects.size(); i++) {
            var obj = objects.get(i);
            if (mtrLt != null) {
                obj.setMatrixLt(mtrLt.get(i));
            }
            if (mtrModel != null) {
                obj.setModelMatrix(mtrModel.get(i));
            }
        }
        if (namedObjectsId != null && !namedObjectsId.isEmpty()) {
            for (int i = 0; i < namedObjectsId.size(); i++) {
                objects.get(namedObjectsId.get(i)).setName(namedObjectsName.get(i));
            }
        }


        Chunk chunk = Chunk.read(source);
        while (!chunk.isTerminator()) {
            switch (chunk.id()) {
                case 0 -> readInfo(source, serializer.version);
                case 2 -> readStreams(source, chunk);
                case 3 -> readGeoms(source, chunk);
                case 4 -> readSplits(source, serializer.version, chunk);
                case 5 -> readGeomRefs(source, chunk);
                default -> System.out.println("Unhandled chunk: " + chunk.id());
            }
            if (chunk.endOffset() != source.position()) {
                System.err.printf("GeometryManager::Callback: Under/over read of chunk %d. Expected %d, got %d%n", chunk.id(), chunk.endOffset(), source.position());
                source.position(chunk.endOffset());
            }
            chunk = Chunk.read(source);
        }
    }

    private void readInfo(DataSource source, int version) throws IOException {
        rootObjId = source.readShort();
        info = GeometryManagerInfo.read(source, version);
    }

    private void readStreams(DataSource source, Chunk chunk) throws IOException {
        streams = new ArrayList<>(info.streamCount);
        for (int i = 0; i < info.streamCount; i++) {
            streams.add(new ObjGeomStream());
        }
        while (source.position() < chunk.endOffset()) {
            var subChunk = Chunk.read(source);
            switch (subChunk.id()) {
                case 0 -> {
                    for (ObjGeomStream stream : streams) {
                        stream.fvf = new FVFSerializer().load(source);
                    }
                }
                case 1 -> {
                    for (ObjGeomStream stream : streams) {
                        stream.stride = source.readShort();
                    }
                }
                case 2 -> {
                    for (ObjGeomStream stream : streams) {
                        stream.size = source.readInt();
                    }
                }

                case 3 -> {
                    state = GeometryManagerState.NO_STREAMING;
                    for (ObjGeomStream stream : streams) {
                        stream.data = source.readBytes(stream.size);
                        stream.state |= 2;
                    }
                }

                case 4 -> {
                    for (ObjGeomStream stream : streams) {
                        var flags = new FioBitSetFlagsSerializer().load(source);
                        stream.flags.set(0, flags.get(0));
                        stream.flags.set(1, flags.get(1));
                    }
                }

                default -> System.out.println("Unhandled subChunk: " + subChunk);
            }

            source.position(subChunk.endOffset());
        }

        if (vBufferMapping != null) {
            Check.state(streams.size() == vBufferMapping.streamToVBuffer.size());
            for (int i = 0; i < streams.size(); i++) {
                streams.get(i).setvBuffOffset(vBufferMapping.streamToVBuffer.get(i).vBufOffset);
            }
        }

    }

    private void readGeoms(DataSource source, Chunk chunk) throws IOException {
        if (info.geomCount == 0) {
            return;
        }
        geoms = new ArrayList<>(info.geomCount);
        for (int i = 0; i < info.geomCount; i++) {
            geoms.add(new ObjGeom());
        }

        while (source.position() < chunk.endOffset()) {
            var subChunk = Chunk.read(source);
            if (subChunk.id() == 0) {
                for (ObjGeom geom : geoms) {
                    geom.flags.addAll(new FVFSerializer().load(source));
                }
            } else if (subChunk.id() == 2) {
                break;
            } else {
                System.out.println("Unhandled SubChunk: " + subChunk);
            }
            source.position(subChunk.endOffset());
        }
        for (ObjGeom geom : geoms) {
            var streamCount = source.readByte();

            if (streamCount == 0) {
                continue;
            }

            var currentStreamIndex = 0;
            while (currentStreamIndex < streamCount) {
                var streamId = source.readInt();
                var streamOffset = source.readInt();

                var stream = streams.get(streamId);

                if (stream.fvf.isEmpty()) {
                    if (!geom.streams.containsKey(GeomStreamSlot.OBJ_GEOM_STRM_FACE)) {
                        geom.streams.put(GeomStreamSlot.OBJ_GEOM_STRM_FACE, stream);
                        geom.streamsOffset.put(GeomStreamSlot.OBJ_GEOM_STRM_FACE, streamOffset);
                    }
                } else {
                    for (GeomStreamSlot slot : EnumSet.range(GeomStreamSlot.OBJ_GEOM_STRM_VERT, GeomStreamSlot.OBJ_GEOM_STRM_INSTANCED)) {
                        if (!geom.streams.containsKey(slot)) {
                            geom.streams.put(slot, stream);
                            geom.streamsOffset.put(slot, streamOffset);
                            geom.fvf.addAll(stream.fvf);
                            break;
                        }
                    }
                }
                ++currentStreamIndex;
            }
        }

        while (source.position() < chunk.endOffset()) {
            var subChunk = Chunk.read(source);
            source.position(subChunk.endOffset());
        }
    }

    private void readSplits(DataSource source, int version, Chunk chunk) throws IOException {
        splits = new ArrayList<>(info.splitsCount);
        for (int i = 0; i < info.splitsCount; i++) {
            splits.add(new ObjSplit());
        }

        while (source.position() < chunk.endOffset()) {
            var subChunk = Chunk.read(source);
            switch (subChunk.id()) {
                case 0 -> {
                    for (ObjSplit split : splits) {
                        split.startVert = Short.toUnsignedInt(source.readShort());
                        split.nVert = Short.toUnsignedInt(source.readShort());
                        split.startFace = Short.toUnsignedInt(source.readShort());
                        split.nFace = Short.toUnsignedInt(source.readShort());
                        split.numInst = Short.toUnsignedInt(source.readShort());

                        split.skinCompoundId = source.readShort();
                        if (version >= 3) {
                            var mask = BitSet.valueOf(source.readBytes(4));
                            var offset = 0;
                            for (int i = 0; i < 32; i++) {
                                if (mask.get(i)) {
                                    split.texDensity.u[offset] = source.readFloat();
                                    split.texDensity.v[offset] = source.readFloat();
                                }
                                offset++;
                            }
                        }
                    }
                }
                case 1 -> {
                    for (ObjSplit split : splits) {
                        var geomId = source.readInt();
                        split.geom = geoms.get(geomId);
                    }
                }
                case 2 -> {
                    System.out.println("Materials AAAAAAAAAAAAAAAAAAAAAAAAAAA");
                }
                case 4 -> {
                    for (ObjSplit split : splits) {
                        var val = source.readByte();
                        for (byte i = 0; i < val; i++) {
                            int idx = Byte.toUnsignedInt(source.readByte());
                            int tile = Short.toUnsignedInt(source.readShort());
                            split.texCoordMaxTile.put(idx, tile);
                        }
                    }
                }
                case 5 -> {
                    for (ObjSplit split : splits) {
                        if (split.geom.fvf.contains(FVF.VERT_COMPR)) {
                            split.vertCompParams = new VertCompressParams(new Vector3(source.readShort(), source.readShort(), source.readShort()), new Vector3(Short.toUnsignedInt(source.readShort()), Short.toUnsignedInt(source.readShort()), Short.toUnsignedInt(source.readShort())));
                        }
                    }
                }

                case 8 -> {
                    int max = 0;
                    for (int i = 0; i < splits.size(); i++) {
                        var slot = source.readShort();
                        var psBin = PsSectionBinary.parseFromDataSource(source);
                        if (slot > max) {
                            max = slot;
                        }
                        // Check.index(slot,splits.size());
                        splits.get(i).materialInfo = psBin;
                    }
                }

                default -> {
                    System.out.println("SubChunk: " + subChunk);
                }
            }
            if (subChunk.endOffset() != source.position()) {
                System.err.printf("GeometryManager: Under/over read of chunk %d. Expected %d, got %d%n", subChunk.id(), subChunk.endOffset(), source.position());
                source.position(subChunk.endOffset());
            }
        }
    }

    private void readGeomRefs(DataSource source, Chunk chunk) throws IOException {
        while (source.position() < chunk.endOffset()) {
            var subChunk = Chunk.read(source);
            switch (subChunk.id()) {
                case 0 -> {
                    geomSetsInfo = new ObjGeomSetsInfo();
                    geomSetsInfo.setStreamRefSets(new FioArraySerializer<>(ObjGeomStreamSetRef::new, new ObjGeomStreamSetRefSerializer()).load(source));
                    geomSetsInfo.setStreamRefs(new FioArraySerializer<>(ObjGeomStreamRef::new, new ObjGeomSteamRefSerializer()).load(source));
                }
                case 1 -> {
                }
                case 2 -> {
                    var objectsSetIds = new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer()).load(source);
                    if (objectsSetIds.size() > 1) {
                        for (int i = 0; i < objectsSetIds.size(); i++) {
                            objects.get(i).setSetId(objectsSetIds.get(i).intValue());
                        }
                    } else if (objectsSetIds.size() == 1) {
                        objects.getFirst().setSetId(objectsSetIds.getFirst().intValue());
                    }
                    geomSetsInfo.setStreamingAvailable(true);
                }
                default -> System.out.println("SubChunk: " + subChunk);
            }

            source.position(subChunk.endOffset());
        }
    }

    private record GeometryManagerInfo(int objectCount, int streamCount, int geomCount, int splitsCount, int psCount,
                                       int unkCount) {
        public static GeometryManagerInfo read(DataSource source, int version) throws IOException {
            int objectCount = 0;
            if (version > 0) {
                objectCount = source.readInt();
            }
            int streamCount = source.readInt();
            int geomCount = source.readInt();
            int splitsCount = source.readInt();
            int psCount = source.readInt();
            int unkCount = source.readInt();
            return new GeometryManagerInfo(objectCount, streamCount, geomCount, splitsCount, psCount, unkCount);
        }
    }
}
