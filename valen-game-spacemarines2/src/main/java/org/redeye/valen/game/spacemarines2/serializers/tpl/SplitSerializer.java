package org.redeye.valen.game.spacemarines2.serializers.tpl;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.util.*;

public class SplitSerializer extends FioStructSerializer<Split> {
    public SplitSerializer() {
        super(Split::new, 12, List.of(
            new FioStructMember<>("Name", Split::setName, new FioStringSerializer()),
            new FioStructMember<>("ShortList", Split::setVertRemap, new FioArraySerializer<>(() -> (short) 0, 9, new FioInt16Serializer())),
            new FioStructMember<>("MatrLt", Split::setInvMatrLT, new MatrixSerializer())
        ));
    }
}
