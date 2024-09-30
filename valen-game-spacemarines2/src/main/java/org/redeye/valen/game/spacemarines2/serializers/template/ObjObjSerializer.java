package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjObjSerializer extends FioStructSerializer<ObjObj> {

    public ObjObjSerializer() {
        super(ObjObj::new, 12, List.of(
            new FioStructMember<>("id", ObjObj::setId, new FioInt16Serializer()),
            new FioStructMember<>("Name", ObjObj::setName, new FioStringSerializer()),
            new FioStructMember<>("StateF", ObjObj::setState, new ObjStateSerializer()),
            new FioStructMember<>("ParentId", ObjObj::setParentId, new FioInt16Serializer()),
            new FioStructMember<>("NextId", ObjObj::setNextId, new FioInt16Serializer()),
            new FioStructMember<>("PrevId", ObjObj::setPrevId, new FioInt16Serializer()),
            new FioStructMember<>("ChildId", ObjObj::setChildId, new FioInt16Serializer()),
            new FioStructMember<>("AnimNmb", ObjObj::setAnimNmb, new FioInt16Serializer()),
            new FioStructMember<>("Affixes", ObjObj::setAffixes, new FioStringSerializer()),
            new FioStructMember<>("MatrixLt", ObjObj::setMatrixLt, new MatrixSerializer()),
            new FioStructMember<>("ModelMatrix", ObjObj::setModelMatrix, new MatrixSerializer()),
            new FioStructMember<>("GeomData", ObjObj::setGeomData, new ObjGeomDataSerializer()),
            new FioStructMember<>("SourceId", ObjObj::setSourceId, new FioStringSerializer()),
            new FioStructMember<>("OBB", (objObj, s) -> {
                if (objObj.geomData != null) objObj.geomData.setObb(s);
            }, new ObbSerializer()),
            new FioStructMember<>("Name2", ObjObj::setName2, new FioOldStringSerializer()),
            new FioStructMember<>("Affixes", ObjObj::setAffixes, new FioOldStringSerializer()),
            new FioStructMember<>("ObjectName", ObjObj::setObjectName, new FioStringSerializer()),
            new FioStructMember<>("Affixes", ObjObj::setAffixes, new FioStringSerializer())
        ));
    }
}
