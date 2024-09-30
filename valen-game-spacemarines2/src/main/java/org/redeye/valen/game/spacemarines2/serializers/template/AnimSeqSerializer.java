package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class AnimSeqSerializer extends FioStructSerializer<AnimSeq> {
    public AnimSeqSerializer() {
        super(AnimSeq::new, 12, List.of(
            new FioStructMember<>("Name", AnimSeq::setName, new FioStringSerializer()),
            new FioStructMember<>("LayerId", AnimSeq::setLayerId, new FioInt32Serializer()),
            new FioStructMember<>("StartFrame", AnimSeq::setStartFrame, new FioFloatSerializer()),
            new FioStructMember<>("EndFrame", AnimSeq::setEndFrame, new FioFloatSerializer()),
            new FioStructMember<>("OffsetFrame", AnimSeq::setOffsetFrame, new FioFloatSerializer()),
            new FioStructMember<>("LenFrame", AnimSeq::setLenFrame, new FioFloatSerializer()),
            new FioStructMember<>("TimeSec", AnimSeq::setTimeSec, new FioFloatSerializer()),
            new FioStructMember<>("ActionFrames", AnimSeq::setActionFrames, new FioArraySerializer<>(ActionFrame::new, 9, new ActionFrameSerializer())),
            new FioStructMember<>("Bbox", AnimSeq::setBbox, new BBoxSerializer())
        ));
    }
}
