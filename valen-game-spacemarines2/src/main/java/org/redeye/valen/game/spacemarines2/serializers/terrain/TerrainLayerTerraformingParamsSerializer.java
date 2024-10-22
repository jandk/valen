package org.redeye.valen.game.spacemarines2.serializers.terrain;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.util.*;

public class TerrainLayerTerraformingParamsSerializer extends FioStructSerializer<TerrainLayerTerraformingParams> {

    public TerrainLayerTerraformingParamsSerializer() {
        super(TerrainLayerTerraformingParams::new, List.of(
            new FioStructMember<>("IsStatic", TerrainLayerTerraformingParams::setStatic, new FioBoolSerializer()),
            new FioStructMember<>("Malleability", TerrainLayerTerraformingParams::setMalleability, new FioFloatSerializer()),
            new FioStructMember<>("UnkString", TerrainLayerTerraformingParams::setUnkString, new FioStringSerializer())
        ));
    }
}
