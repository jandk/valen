package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class SplitSerializer extends FioStructSerializer<Split> {
    public SplitSerializer() {
        super(Split::new, List.of(
            new FioStructMember<>("Name", Split::setName, new FioStringSerializer()),
            new FioStructMember<>("ShortList", Split::setVertRemap, new FioArraySerializer<>(() -> (short) 0, new FioInt16Serializer())),
            new FioStructMember<>("MatrLt", Split::setInvMatrLT, new MatrixSerializer())
        ));
    }
}
