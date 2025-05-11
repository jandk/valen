package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class HairReader implements AssetReader<Model, GreatCircleAsset> {
    private final GreatCircleArchive archive;

    public HairReader(GreatCircleArchive archive) {
        this.archive = Objects.requireNonNull(archive);
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.hair;
    }

    @Override
    public Model read(DataSource source, GreatCircleAsset asset) throws IOException {
        var hairMesh = HairMesh.read(source);
        source.expectEnd();

        Hair hair = map(hairMesh, asset);
        return new Model(List.of(), Axis.Z).withHair(Optional.of(hair));
    }

    private Hair map(HairMesh hairMesh, GreatCircleAsset asset) {
        var segments = getSegments(hairMesh.particleSumPerStrand());
        HairHeader header = hairMesh.header();
        var positions = getPositions(
            hairMesh.sourcePositions(),
            header.compressionPosScale(),
            header.compressionPosBias()
        );

        return new Hair(
            asset.id().fullName(),
            IntBuffer.wrap(segments),
            FloatBuffer.wrap(positions)
        );
    }

    private static int[] getSegments(int[] particleSumPerStrand) {
        var segments = new int[particleSumPerStrand.length];
        segments[0] = particleSumPerStrand[0] - 1;
        for (var i = 1; i < segments.length - 1; i++) {
            segments[i] = particleSumPerStrand[i] - particleSumPerStrand[i - 1] - 1;
        }
        return segments;
    }

    private float[] getPositions(short[] sourcePositions, float scale, Vector3 bias) {
        float[] positions = new float[sourcePositions.length * 3 / 4];
        for (int i = 0, o = 0; i < sourcePositions.length; i += 4, o += 3) {
            positions[o/**/] = Math.fma(MathF.unpackUNorm16(sourcePositions[i/**/]), scale, bias.x());
            positions[o + 1] = Math.fma(MathF.unpackUNorm16(sourcePositions[i + 1]), scale, bias.y());
            positions[o + 2] = Math.fma(MathF.unpackUNorm16(sourcePositions[i + 2]), scale, bias.z());
        }
        return positions;
    }
}
