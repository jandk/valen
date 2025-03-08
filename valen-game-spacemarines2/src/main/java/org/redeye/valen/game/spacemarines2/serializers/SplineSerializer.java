package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.spline.*;

import java.io.*;

public class SplineSerializer implements FioSerializer<Spline> {
    @Override
    public Spline load(DataSource source) throws IOException {
        var chunk = Chunk.read(source);

        var spline = new Spline();

        while (chunk.id() != 1) {
            switch (chunk.id()) {
                case 0xF0 -> spline.setType(SplineType.values()[source.readByte()]);
                case 0xF1 -> spline.setCompressedDataSize(source.readByte());
                case 0xF2 -> spline.setValueDim(source.readByte());
                case 0xF3 -> spline.setDataDim(source.readByte());
                case 0xF4 -> spline.setPointCount(source.readInt());
                case 0xF5 -> spline.setDataSize(source.readInt());
                case 0xF6 -> {
                    byte[] splineData = source.readBytes(spline.getDataSize());
                    try (DataSource splineSrc = DataSource.fromArray(splineData)) {
                        float[] data = readData(spline.getCompressedDataSize(), spline.getDataSize(), splineSrc);
                        switch (spline.getType()) {
                            case Linear1D -> spline.setData(new SplineLinear1D(data));
                            case Linear3D -> spline.setData(new SplineLinear3D(data));
                            case Quat -> spline.setData(new SplineQuat(data));
                            default -> System.out.println("Unsupported spline type: " + spline.getType());
                        }
                    }
                }
                default -> {
                }
            }
            if (chunk.endOffset() != source.position()) {
                System.err.printf("Under/over read of %x spline chunk. Expected %d, got %d%n%n", chunk.id(), chunk.endOffset(), source.position());
                source.position(chunk.endOffset());
            }
            chunk = Chunk.read(source);
        }
        return spline;
    }

    @Override
    public int flags() {
        return 0;
    }


    private static float[] readData(int compression, int byteCount, DataSource source) throws IOException {
        switch (compression) {
            case 2 -> {
                var data = new float[byteCount / 2];
                for (int i = 0; i < data.length; i++) {
                    data[i] = source.readShort() / 32767.f;
                }
                source.skip(byteCount % 2);
                return data;
            }
            case 0 -> {
                return source.readFloats(byteCount / 4);
            }
            default -> throw new IllegalStateException("Unexpected value: " + compression);
        }
    }
}
