package be.twofold.valen.ui.component.rawview;

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
            while (buffer.hasRemaining()) {
                int b0 = buffer.get() & 0xFF;
                if ((b0 & 0x80) == 0) {
                    if (b0 == 0x00) {
                        return false;
                    }
                } else if ((b0 & 0xE0) == 0xC0) {
                    if (buffer.remaining() < 1) {
                        return false;
                    }
                    int b1 = buffer.get() & 0xFF;
                    if (b0 < 0xC2 || badTail(b1)) {
                        return false;
                    }
                } else if ((b0 & 0xF0) == 0xE0) {
                    if (buffer.remaining() < 2) {
                        return false;
                    }

                    int b1 = buffer.get() & 0xFF;
                    if ((b0 == 0xE0) && (b1 < 0xA0 || b1 > 0xBF) ||
                        (b0 >= 0xE1 && b0 <= 0xEC) && badTail(b1) ||
                        (b0 == 0xED) && (b1 < 0x80 || b1 > 0x9F) ||
                        (b0 >= 0xEE && b0 <= 0xEF) && badTail(b1)) {
                        return false;
                    }

                    int b2 = buffer.get() & 0xFF;
                    if (badTail(b2)) {
                        return false;
                    }
                } else if ((b0 & 0xF8) == 0xF0) {
                    if (buffer.remaining() < 3) {
                        return false;
                    }

                    int b1 = buffer.get() & 0xFF;
                    if ((b0 == 0xF0) && (b1 < 0x90 || b1 > 0xBF) ||
                        (b0 >= 0xF1 && b0 <= 0xF3) && badTail(b1) ||
                        (b0 == 0xF4) && (b1 < 0x80 || b1 > 0x8F)) {
                        return false;
                    }

                    int b2 = buffer.get() & 0xFF;
                    int b3 = buffer.get() & 0xFF;
                    if (badTail(b2) || badTail(b3)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean badTail(int i) {
            return (i & 0xC0) != 0x80;
        }
    }
}
