package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import be.twofold.valen.core.geometry.*;

public final class CastHairMapper {
    public void map(CastNodes.Model modelNode, Hair hair) {
        modelNode.createHair()
            .setName(hair.name())
                .setSegmentsBuffer(hair.segments().asBuffer())
                .setParticleBuffer(hair.positions().asBuffer());
    }
}
