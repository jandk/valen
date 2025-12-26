package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.util.*;

public final class HairReader implements AssetReader<Model, GreatCircleAsset> {
    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.hair;
    }

    @Override
    public Model read(BinarySource source, GreatCircleAsset asset) throws IOException {
        var hairMesh = HairMesh.read(source);
        source.expectEnd();

        var hair = map(hairMesh, asset);
        return new Model(List.of(), Axis.Z)
            .withHair(Optional.of(hair));
    }

    private Hair map(HairMesh hairMesh, GreatCircleAsset asset) {
        var segments = getSegments(hairMesh.particleSumPerStrand());
        var header = hairMesh.header();
        var positions = getPositions(
            hairMesh.sourcePositions(),
            header.compressionPosScale(),
            header.compressionPosBias()
        );

        return new Hair(asset.id().fullName(), segments, positions);
    }

    private Ints getSegments(Ints particleSumPerStrand) {
        var segments = new int[particleSumPerStrand.length()];
        segments[0] = particleSumPerStrand.get(0) - 1;
        for (var i = 1; i < segments.length - 1; i++) {
            segments[i] = particleSumPerStrand.get(i) - particleSumPerStrand.get(i - 1) - 1;
        }
        return Ints.wrap(segments);
    }

    private Floats getPositions(Shorts sourcePositions, float scale, Vector3 bias) {
        var positions = new float[sourcePositions.length() * 3 / 4];
        for (int i = 0, o = 0; i < sourcePositions.length(); i += 4, o += 3) {
            positions[o/**/] = Math.fma(MathF.unpackUNorm16(sourcePositions.get(i/**/)), scale, bias.x());
            positions[o + 1] = Math.fma(MathF.unpackUNorm16(sourcePositions.get(i + 1)), scale, bias.y());
            positions[o + 2] = Math.fma(MathF.unpackUNorm16(sourcePositions.get(i + 2)), scale, bias.z());
        }
        return Floats.wrap(positions);
    }
}
