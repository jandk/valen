package org.redeye.valen.game.spacemarines2.serializers.terrain;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.io.*;
import java.util.*;

public class TerrainDeformPersistentDataSerializer extends FioStructSerializer<TerrainDeformPersistentData> {

    public TerrainDeformPersistentDataSerializer() {
        super(TerrainDeformPersistentData::new, List.of(
            new FioStructMember<>("blocksInAtlas", (obj, value) -> {
                obj.blocksInAtlas = List.of(value, value, value, value);
            }, new FioInt32Serializer()),
            new FioStructMember<>("blockIdxToAtlasBlockNum", (obj, value) -> {
                obj.blockIdxToAtlasBlockNum = List.of(value, value, value, value);
            }, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("blockIdxToAtlasBlockNum", (obj, value) -> {
                obj.blockIdxToAtlasBlockNum = List.of(value, value, value, value);
            }, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("blockIdxToAtlasBlockNum", (obj, value) -> {
                obj.blockIdxToAtlasBlockNum = List.of(value, value, value, value);
            }, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("unk", (obj, value) -> {
                System.out.println(value);
            }, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("blockIdxToAtlasBlockNum", (obj, value) -> {
                obj.blockIdxToAtlasBlockNum = value;
            }, new FioSerializer<List<List<Integer>>>() {
                @Override
                public List<List<Integer>> load(DataSource source) throws IOException {
                    return List.of(
                        new FioArraySerializer<>(() -> 0, new FioInt32Serializer()).load(source),
                        new FioArraySerializer<>(() -> 0, new FioInt32Serializer()).load(source),
                        new FioArraySerializer<>(() -> 0, new FioInt32Serializer()).load(source),
                        new FioArraySerializer<>(() -> 0, new FioInt32Serializer()).load(source)
                    );
                }

                @Override
                public int flags() {
                    return 0;
                }
            })
        ));
    }

    @Override
    public int flags() {
        return 44;
    }
}
