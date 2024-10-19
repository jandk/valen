package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class AnimTplSerializer extends FioStructSerializer<AnimTemplate> {
    public static final int SIGNATURE = 'T' | 'P' << 8 | 'L' << 16 | '1' << 24; // TPL1

    public AnimTplSerializer() {
        super(SIGNATURE, AnimTemplate::new, List.of(
            new FioStructMember<>("Name", AnimTemplate::setName, new FioStringSerializer()),
            new FioStructMember<>("NameClass", AnimTemplate::setNameClass, new FioStringSerializer()),
            new FioStructMember<>("State", AnimTemplate::setState, new FioBitSetFlagsSerializer()),
            new FioStructMember<>("Affix", AnimTemplate::setAffix, new FioStringSerializer()),
            new FioStructMember<>("Ps", AnimTemplate::setPs, new FioStringSerializer()),
            new FioStructMember<>("Skin", AnimTemplate::setSkin, new TplSkinSerializer()),
            new FioStructMember<>("TplAnimTrack", AnimTemplate::setTplAnimTrack, new AnimTrackSerializer()),
            FioStructMember.nullMember(),
            new FioStructMember<>("BBox", AnimTemplate::setBBox, new BBoxSerializer()),
            new FioStructMember<>("LodDef", AnimTemplate::setLodDef, new FioArraySerializer<>(LodDef::new, new LodDefSerializer())),
            new FioStructMember<>("TexList", AnimTemplate::setTexList, new TxmTexListSerializer()),
            new FioStructMember<>("Geometry", AnimTemplate::setGeometryManager, new GeometryManagerSerializer()),
            new FioStructMember<>("ExternData", AnimTemplate::setExternData, new TplExternDataSerializer())
        ));
    }
}
