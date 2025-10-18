package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.util.*;

public final class StrandsHairReader implements AssetReader<Model, DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.StrandsHair;
    }

    @Override
    public Model read(BinaryReader reader, DarkAgesAsset asset) throws IOException {
        var strandsHair = StrandsHair.read(reader);

        var segments = getSegments(strandsHair.strands());
        var positions = getPositions(
            strandsHair.particles(),
            strandsHair.header().compressionPosScale(),
            strandsHair.header().compressionPosBias()
        );

        var hair = new Hair(asset.id().fileName(), segments, positions);
        return new Model(List.of(), Axis.Z)
            .withHair(Optional.of(hair));
    }

    private Ints getSegments(int[] strands) {
        var result = new int[strands.length];
        result[0] = strands[0] - 1;
        for (var i = 1; i < result.length - 1; i++) {
            result[i] = strands[i] - strands[i - 1] - 1;
        }
        return Ints.wrap(result);
    }

    private Floats getPositions(short[] sourcePositions, float scale, Vector3 bias) {
        var positions = new float[sourcePositions.length * 3 / 4];
        for (int i = 0, o = 0; i < sourcePositions.length; i += 4, o += 3) {
            positions[o/**/] = Math.fma(MathF.unpackUNorm16(sourcePositions[i/**/]), scale, bias.x());
            positions[o + 1] = Math.fma(MathF.unpackUNorm16(sourcePositions[i + 1]), scale, bias.y());
            positions[o + 2] = Math.fma(MathF.unpackUNorm16(sourcePositions[i + 2]), scale, bias.z());
        }
        return Floats.wrap(positions);
    }
}
