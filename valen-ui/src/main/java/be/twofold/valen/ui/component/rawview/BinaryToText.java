package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.util.*;

import java.nio.charset.*;
import java.util.*;

final class BinaryToText {
    private final List<Converter> converters = List.of(
        new BomConverter(),
        new Utf8Converter()
    );

    public Optional<String> binaryToText(byte[] array) {
        return binaryToText(array, 0, array.length);
    }

    public Optional<String> binaryToText(byte[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);

        return converters.stream()
            .flatMap(converter -> converter.convert(array, offset, length).stream())
            .findFirst();
    }

    private interface Converter {
        Optional<String> convert(byte[] array, int offset, int length);
    }

    static final class BomConverter implements Converter {
        @Override
        public Optional<String> convert(byte[] array, int offset, int length) {
            return Arrays.stream(ByteOrderMark.values())
                .filter(bom -> checkSingleBom(array, offset, length, bom))
                .findFirst()
                .map(bom -> new String(array, offset + bom.length(), length - bom.length(), bom.charset()));
        }

        private boolean checkSingleBom(byte[] array, int offset, int length, ByteOrderMark bom) {
            return bom.length() <= length && Arrays.compare(
                array, offset, offset + bom.length(),
                bom.bytes(), 0, bom.length()
            ) == 0;
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
        public Optional<String> convert(byte[] array, int offset, int length) {
            return isValid(array, offset, length)
                ? Optional.of(new String(array, offset, length, StandardCharsets.UTF_8))
                : Optional.empty();
        }

        private boolean isValid(byte[] array, int offset, int length) {
            int index = offset;
            int limit = offset + length;

            while (index < limit) {
                int b0 = array[index++] & 0xFF;
                if ((b0 & 0x80) == 0) {
                    if (b0 == 0x00) {
                        return false;
                    }
                } else if ((b0 & 0xE0) == 0xC0) {
                    if (index >= limit) {
                        return false;
                    }
                    int b1 = array[index++] & 0xFF;
                    if (b0 < 0xC2 || badTail(b1)) {
                        return false;
                    }
                } else if ((b0 & 0xF0) == 0xE0) {
                    if (index >= limit - 1) {
                        return false;
                    }

                    int b1 = array[index++] & 0xFF;
                    if ((b0 == 0xE0) && (b1 < 0xA0 || b1 > 0xBF) ||
                        (b0 >= 0xE1 && b0 <= 0xEC) && badTail(b1) ||
                        (b0 == 0xED) && (b1 < 0x80 || b1 > 0x9F) ||
                        (b0 >= 0xEE && b0 <= 0xEF) && badTail(b1)) {
                        return false;
                    }

                    int b2 = array[index++] & 0xFF;
                    if (badTail(b2)) {
                        return false;
                    }
                } else if ((b0 & 0xF8) == 0xF0) {
                    if (index >= limit - 2) {
                        return false;
                    }

                    int b1 = array[index++] & 0xFF;
                    if ((b0 == 0xF0) && (b1 < 0x90 || b1 > 0xBF) ||
                        (b0 >= 0xF1 && b0 <= 0xF3) && badTail(b1) ||
                        (b0 == 0xF4) && (b1 < 0x80 || b1 > 0x8F)) {
                        return false;
                    }

                    int b2 = array[index++] & 0xFF;
                    int b3 = array[index++] & 0xFF;
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
