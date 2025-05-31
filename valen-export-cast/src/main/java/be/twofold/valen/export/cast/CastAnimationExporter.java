package be.twofold.valen.export.cast;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.export.cast.mappers.*;
import be.twofold.valen.format.cast.*;

import java.nio.file.*;

public final class CastAnimationExporter extends CastExporter<Animation> {
    private final CastAnimationMapper animationMapper = new CastAnimationMapper();

    @Override
    public String getID() {
        return "animation.cast";
    }

    @Override
    public Class<Animation> getSupportedType() {
        return Animation.class;
    }

    @Override
    public void doExport(Animation value, CastNode.Root root, Path castPath, Path imagePath) {
        animationMapper.map(value, root);
    }
}
