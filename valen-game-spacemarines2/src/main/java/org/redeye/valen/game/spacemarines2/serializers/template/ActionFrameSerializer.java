package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ActionFrameSerializer extends FioStructSerializer<ActionFrame> {
    public ActionFrameSerializer() {
        super(ActionFrame::new, List.of(
            new FioStructMember<>("Frame", ActionFrame::setFrame, new FioInt32Serializer()),
            new FioStructMember<>("Comment", ActionFrame::setComment, new FioStringSerializer()),
            new FioStructMember<>("Flags", ActionFrame::setFlags, new FioArraySerializer<>(() -> "", new FioStringSerializer()))
        ));
    }
}
