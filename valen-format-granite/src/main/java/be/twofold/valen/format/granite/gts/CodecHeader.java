package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.enums.*;

import java.io.*;

public sealed interface CodecHeader {
    static CodecHeader read(BinaryReader reader, Codec codec) throws IOException {
        return switch (codec) {
            case UNIFORM -> Uniform.read(reader);
            case BC -> BC.read(reader);
            default -> throw new UnsupportedOperationException(codec.toString());
        };
    }

    record BC(
        short magic,
        String algorithm,
        String algorithmVersion,
        byte always0,
        DataType dataType,
        int textureFormat,
        byte always1
    ) implements CodecHeader {
        private static BC read(BinaryReader reader) throws IOException {
            var magic = reader.readShort();
            var algorithm = reader.readString(16).trim();
            var algorithmVersion = reader.readString(16).trim();
            reader.expectPadding(6);
            var always0 = reader.readByte();
            var dataType = DataType.fromValue(reader.readByte());
            reader.expectPadding(2);
            var textureFormat = reader.readInt();
            reader.expectPadding(1);
            var always1 = reader.readByte();
            reader.expectPadding(6);

            return new BC(
                magic,
                algorithm,
                algorithmVersion,
                always0,
                dataType,
                textureFormat,
                always1
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
        private static Uniform read(BinaryReader reader) throws IOException {
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
