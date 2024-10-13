package org.redeye.valen.game.spacemarines2.serializers;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class BlockInfoSerializer extends FioStructSerializer<BlockInfo> {
    public BlockInfoSerializer() {
        super(BlockInfo::new, 12, List.of(
            new FioStructMember<>("GlobalInstId", BlockInfo::setGlobalInstId, new FioInt32Serializer()),
            new FioStructMember<>("TplIdxNInst", BlockInfo::setTplIdxNInst, new FioInt32Serializer()),
            new FioStructMember<>("TransIdxLwiIdx", BlockInfo::setTransIdxLwiIdx, new FioInt32Serializer()),
            new FioStructMember<>("PivotIdx", BlockInfo::setPivotIdx, new FioInt32Serializer()),
            new FioStructMember<>("PresetGridIdxHigh", BlockInfo::setPresetGridIdxHigh, new FioInt32Serializer()),
            new FioStructMember<>("PresetGridIdxLow", BlockInfo::setPresetGridIdxLow, new FioInt32Serializer()),
            new FioStructMember<>("ScaleCorrection", BlockInfo::setScaleCorrection, new FioFloatSerializer())
        ));
    }
}
