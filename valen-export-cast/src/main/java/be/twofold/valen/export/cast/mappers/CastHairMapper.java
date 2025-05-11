package be.twofold.valen.export.cast.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.cast.*;

public final class CastHairMapper {
    public void map(CastNode.Model modelNode, Hair hair) {
        modelNode.createHair()
            .setName(hair.name())
            .setSegmentsBuffer(hair.segments())
            .setParticleBuffer(hair.positions());
    }
}
