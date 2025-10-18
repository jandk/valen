package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

final class BinaryToText {
    private final List<Converter> converters = List.of(
        new BomConverter(),
        new Utf8Converter()
    );

    public Optional<String> binaryToText(ByteBuffer buffer) {
        return converters.stream()
            .flatMap(converter -> converter.convert(buffer).stream())
            .findFirst();
    }

    private interface Converter {
        Optional<String> convert(ByteBuffer buffer);
    }

    static final class BomConverter implements Converter {
        @Override
        public Optional<String> convert(ByteBuffer buffer) {
            return Arrays.stream(ByteOrderMark.values())
                .filter(bom -> checkSingleBom(buffer, bom))
                .findFirst()
                .map(bom -> bom.charset().decode(buffer.position(bom.length())).toString());
        }

        private boolean checkSingleBom(ByteBuffer buffer, ByteOrderMark bom) {
            return bom.length() <= buffer.remaining() &&
                IntStream.range(0, bom.length())
                    .noneMatch(i -> buffer.get(i) != bom.bytes()[i]);
        }

        private enum ByteOrderMark {
            UTF_8(StandardCharsets.UTF_8, (byte) 0xEF, (byte) 0xBB, (byte) 0xBF),
            UTF_16LE(StandardCharsets.UTF_16LE, (byte) 0xFF, (byte) 0xFE),
            UTF_16BE(StandardCharsets.UTF_16BE, (byte) 0xFE, (byte) 0xFF),
            UTF_32LE(StandardCharsets.UTF_32LE, (byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00),
            UTF_32BE(StandardCharsets.UTF_32BE, (byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF),
            ;
            private final Charset charset;
            private final byte[] bytes;

            ByteOrderMark(Charset charset, byte... bytes) {
                this.charset = charset;
                this.bytes = bytes;
            }

            public Charset charset() {
                return charset;
            }

            public byte[] bytes() {
                return bytes;
            }

            public int length() {
                return bytes.length;
            }
        }
    }

    static final class Utf8Converter implements Converter {
        @Override
        public Optional<String> convert(ByteBuffer buffer) {
            return isValid(buffer.slice())
                ? Optional.of(StandardCharsets.UTF_8.decode(buffer).toString())
                : Optional.empty();
        }

        private boolean isValid(ByteBuffer buffer) {
            return Utf8.isValid(
                buffer.array(),
                buffer.arrayOffset() + buffer.position(),
                buffer.arrayOffset() + buffer.limit()
            );
        }
    }
}
