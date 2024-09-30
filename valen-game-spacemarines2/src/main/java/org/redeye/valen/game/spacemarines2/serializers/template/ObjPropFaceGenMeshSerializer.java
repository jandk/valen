package org.redeye.valen.game.spacemarines2.serializers.template;

import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.util.*;

public class ObjPropFaceGenMeshSerializer extends FioStructSerializer<ObjPropFaceGenMesh> {
    public ObjPropFaceGenMeshSerializer() {
        super(ObjPropFaceGenMesh::new, 12, List.of(
            new FioStructMember<>("internal", (holder, value) -> {
                holder.setProject(value.name);
                holder.setPart(value.name);
                holder.setInvMatr(value.invMatrLT);
                holder.setVertRemap(value.vertRemap);
            }, new SplitSerializer()),
            new FioStructMember<>("Controls", ObjPropFaceGenMesh::setControls, new FioStringSerializer()),
            new FioStructMember<>("Project", ObjPropFaceGenMesh::setProject, new FioStringSerializer()),
            new FioStructMember<>("Part", ObjPropFaceGenMesh::setPart, new FioStringSerializer()),
            new FioStructMember<>("VertRemap", ObjPropFaceGenMesh::setVertRemap, new FioArraySerializer<>(() -> (short) 0, 9, new FioInt16Serializer())),
            new FioStructMember<>("InvMatr", ObjPropFaceGenMesh::setInvMatr, new MatrixSerializer())
        ));
    }
}
