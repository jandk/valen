package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class AnimTrackSerializer extends FioStructSerializer<TplAnimTrack> {
    public AnimTrackSerializer() {
        super(TplAnimTrack::new, List.of(
            new FioStructMember<>("SeqList", TplAnimTrack::setSeqList, new FioArraySerializer<>(AnimSeq::new, new AnimSeqSerializer())),
            new FioStructMember<>("ObjAnimList", TplAnimTrack::setObjAnimList, new FioArraySerializer<>(AnimObjAnim::new, new AnimObjAnimSerializer())),
            new FioStructMember<>("ObjMapList", TplAnimTrack::setObjMapList, new FioArraySerializer<>(() -> (short) 0, new FioInt16Serializer())),
            new FioStructMember<>("RootAnim", TplAnimTrack::setRootAnim, new AnimRootedSerializer())
        ));
    }
}
