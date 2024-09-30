package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class AnimTrackSerializer extends FioStructSerializer<TplAnimTrack> {
    public AnimTrackSerializer() {
        super(TplAnimTrack::new, 12, List.of(
            new FioStructMember<>("SeqList", TplAnimTrack::setSeqList, new FioArraySerializer<>(AnimSeq::new, 9, new AnimSeqSerializer())),
            new FioStructMember<>("ObjAnimList", TplAnimTrack::setObjAnimList, new FioArraySerializer<>(AnimObjAnim::new, 9, new AnimObjAnimSerializer())),
            new FioStructMember<>("ObjMapList", TplAnimTrack::setObjMapList, new FioArraySerializer<>(() -> (short) 0, 9, new FioInt16Serializer())),
            new FioStructMember<>("RootAnim", TplAnimTrack::setRootAnim, new AnimRootedSerializer())
        ));
    }
}
