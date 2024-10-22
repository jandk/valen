package org.redeye.valen.game.spacemarines2.serializers.terrain;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.util.*;

public class TerrainMaterialSerializer extends FioStructSerializer<TerrainMaterials> {

    public TerrainMaterialSerializer() {
        super(TerrainMaterials::new, List.of(
            new FioStructMember<>("Layers", TerrainMaterials::setLayers, new FioArraySerializer<>(TerrainLayer::new, new TerrainLayerSerializer()))
        ));
    }
}
