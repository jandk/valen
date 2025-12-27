package be.twofold.valen.format.granite.gts;

import be.twofold.valen.format.granite.enums.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public sealed interface CodecHeader {
    static CodecHeader read(BinarySource source, Codec codec) throws IOException {
        return switch (codec) {
            case UNIFORM -> Uniform.read(source);
            case BC -> BC.read(source);
            default -> throw new UnsupportedOperationException(codec.toString());
        };
    }

    record BC(
        String algorithm,
        String algorithmVersion,
        byte always0,
        DataType dataType,
        int textureFormat,
        byte saveMip
    ) implements CodecHeader {
        private static BC read(BinarySource source) throws IOException {
            source.expectShort((short) 9102);
            var algorithm = source.readString(16).trim();
            var algorithmVersion = source.readString(16).trim();
            source.expectInt(0);
            source.expectShort((short) 0);
            var always0 = source.readByte();
            var dataType = DataType.fromValue(source.readByte());
            source.expectShort((short) 0);
            var textureFormat = source.readInt();
            source.expectByte((byte) 0);
            var saveMip = source.readByte();
            source.expectInt(0);
            source.expectShort((short) 0);

            return new BC(
                algorithm,
                algorithmVersion,
                always0,
                dataType,
                textureFormat,
                saveMip
            );
        }

        public Compression getCompression() {
            return switch (algorithm) {
                case "lz77" -> {
                    if (!algorithmVersion.equals("fastlz0.1.0")) {
                        throw new UnsupportedOperationException("Unsupported algorithm version: " + algorithmVersion);
                    }
                    yield Compression.FAST_LZ;
                }
                case "lz4" -> {
                    if (!algorithmVersion.equals("lz40.1.0")) {
                        throw new UnsupportedOperationException("Unsupported algorithm version: " + algorithmVersion);
                    }
                    yield Compression.LZ4;
                }
                case "raw" -> Compression.RAW;
                default -> throw new UnsupportedOperationException("Unsupported algorithm: " + algorithm);
            };
        }
    }

    record Uniform(
        int size,
        int count,
        DataType type
    ) implements CodecHeader {
        private static Uniform read(BinarySource reader) throws IOException {
            reader.expectInt(66);
            var size = reader.readInt();
            var count = reader.readInt();
            var type = DataType.fromValue(reader.readInt());

            return new Uniform(
                size,
                count,
                type
            );
        }
    }
}
