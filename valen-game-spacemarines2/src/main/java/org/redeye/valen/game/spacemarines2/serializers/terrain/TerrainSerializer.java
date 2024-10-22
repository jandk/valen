package org.redeye.valen.game.spacemarines2.serializers.terrain;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.util.*;

public class TerrainSerializer extends FioStructSerializer<Terrain> {
    public TerrainSerializer() {
        super(Terrain::new, List.of(
            new FioStructMember<>("resXArr", Terrain::setResXArr, new FioInt32Serializer()),
            new FioStructMember<>("resZArr", Terrain::setResZArr, new FioInt32Serializer()),
            new FioStructMember<>("unkFloat", Terrain::setUnkFloat, new FioFloatSerializer()),
            new FioStructMember<>("unkFloat2", Terrain::setUnkFloat2, new FioFloatSerializer()),
            new FioStructMember<>("heightArrDeprecatedFloat", Terrain::setHeightArrDeprecatedFloat, new FioArraySerializer<>(() -> 0f, new FioFloatSerializer())),
            new FioStructMember<>("terrainHolesMask", Terrain::setTerrainHolesMask, new FioBitSetSerializer()),
            new FioStructMember<>("staticGameMaterialsOld", Terrain::setStaticGameMaterialsOld, new FioArraySerializer<>(() -> 0, new FioInt32Serializer())),
            new FioStructMember<>("terrainMaterials", Terrain::setTerrainMaterials, new TerrainMaterialSerializer()),
            new FioStructMember<>("extrusionArrDeprecated", Terrain::setExtrusionArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("wetnessArrDeprecated", Terrain::setWetnessArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("weights1ArrDeprecated", Terrain::setWeights1ArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("weights2ArrDeprecated", Terrain::setWeights2ArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("deformPersistentData", Terrain::setDeformPersistentData, new TerrainDeformPersistentDataSerializer()),
            new FioStructMember<>("fogOfWarArr", Terrain::setFogOfWarArr, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("excavatabilityArrDeprecated", Terrain::setExcavatabilityArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("sandArrDeprecated", Terrain::setSandArrDeprecated, new FioArraySerializer<>(() -> (short) 0, new FioInt16Serializer())),
            new FioStructMember<>("asphaltArrDeprecated", Terrain::setAsphaltArrDeprecated, new FioArraySerializer<>(() -> (short) 0, new FioInt16Serializer())),
            new FioStructMember<>("heightArrDeprecatedUINT16", Terrain::setHeightArrDeprecatedUINT16, new FioArraySerializer<>(() -> (short) 0, new FioInt16Serializer())),
            new FioStructMember<>("staticGameMaterialsDeprecated", Terrain::setStaticGameMaterialsDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("extrusionArrAutoDeprecated", Terrain::setExtrusionArrAutoDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("malleabilityArrDeprecated", Terrain::setMalleabilityArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("minViscosityArrDeprecated", Terrain::setMinViscosityArrDeprecated, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("heightArr", Terrain::setHeightArr, new TerrainBlockShortArraySerializer()),
            new FioStructMember<>("extrusionArrAuto", Terrain::setExtrusionArrAuto, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("excavatabilityArr", Terrain::setExcavatabilityArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("extrusionArr", Terrain::setExtrusionArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("wetnessArr", Terrain::setWetnessArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("sandArr", Terrain::setSandArr, new TerrainBlockShortArraySerializer()),
            new FioStructMember<>("asphaltArr", Terrain::setAsphaltArr, new TerrainBlockShortArraySerializer()),
            new FioStructMember<>("malleabilityArr", Terrain::setMalleabilityArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("minViscosityArr", Terrain::setMinViscosityArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("staticGameMaterials", Terrain::setStaticGameMaterials, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("physExportData", Terrain::setPhysExportData, new FioArraySerializer<>(() -> (byte) 0, new FioInt8Serializer())),
            new FioStructMember<>("grassHideMaskArr", Terrain::setGrassHideMaskArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("aiGrassHeightIndexesArr", Terrain::setAiGrassHeightIndexesArr, new TerrainBlockByteArraySerializer()),
            new FioStructMember<>("aiGrassHeights", Terrain::setAiGrassHeights, new FioArraySerializer<>(() -> 0f, new FioFloatSerializer()))

        ));
    }
}
