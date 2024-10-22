package org.redeye.valen.game.spacemarines2.serializers.terrain;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.util.*;

public class TerrainLayerSerializer extends FioStructSerializer<TerrainLayer> {
    public TerrainLayerSerializer() {
        super(TerrainLayer::new, List.of(
            new FioStructMember<>("SizeMetersInvX", TerrainLayer::setSizeMetersInvX, new FioFloatSerializer()),
            new FioStructMember<>("SizeMetersInvY", TerrainLayer::setSizeMetersInvY, new FioFloatSerializer()),
            new FioStructMember<>("unkFloat", TerrainLayer::setUnkFloat, new FioFloatSerializer()),
            new FioStructMember<>("softness", TerrainLayer::setSoftness, new FioFloatSerializer()),
            new FioStructMember<>("wetnessAlbedoFactor", TerrainLayer::setWetnessAlbedoFactor, new FioFloatSerializer()),
            new FioStructMember<>("wetnessRoughnessFactor", TerrainLayer::setWetnessRoughnessFactor, new FioFloatSerializer()),
            new FioStructMember<>("unkFloat2", TerrainLayer::setUnkFloat2, new FioFloatSerializer()),
            new FioStructMember<>("metalness", TerrainLayer::setMetalness, new FioFloatSerializer()),
            new FioStructMember<>("texelDensity", TerrainLayer::setTexelDensity, new FioFloatSerializer()),
            new FioStructMember<>("terraforming", TerrainLayer::setTerraforming, new TerrainLayerTerraformingParamsSerializer())
        ));
    }
}
