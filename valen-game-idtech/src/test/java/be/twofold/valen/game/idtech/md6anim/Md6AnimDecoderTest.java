package be.twofold.valen.game.idtech.md6anim;

import org.junit.jupiter.api.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class Md6AnimDecoderTest {
    private static final int MISS = Md6AnimDecoder.MISSING;

    @Test
    void testDecodeRleEternal() throws IOException {
        var hex = """
            58 05 01 08 07 0C 10 88 01 20 01 23 02 2B 04 35
            0B 3A 07 46 08 4E 07 58 0A 60 04 6B
            """;

        var expected = new int[]{
            0x01, 0x02, 0x03, 0x04, 0x05, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x10, 0x11, 0x12,
            0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, MISS, MISS, MISS, MISS, MISS, MISS, MISS,
            MISS, 0x20, 0x23, 0x2B, 0x2C, 0x35, 0x36, 0x37, 0x38, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3F, 0x40,
            0x41, 0x42, 0x43, 0x44, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C, 0x4E, 0x4F, 0x50, 0x51, 0x52,
            0x53, 0x54, 0x55, 0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5E, 0x60, 0x61, 0x62, 0x63, 0x64, 0x65,
            0x66, 0x67, 0x68, 0x69, 0x6B, 0x6C, 0x6D, 0x6E
        };
        var actual = Md6AnimDecoder.decodeRle(reader(hex), false);
        assertThat(toArray(actual)).isEqualTo(expected);
    }

    @Test
    void testDecodeRleDarkAgesBytes() throws IOException {
        var hex = "08 00 02 35 01 5B 04 64 01 69";

        var expected = new int[]{
            53, 54, 91, 100, 101, 102, 103, 105
        };
        var actual = Md6AnimDecoder.decodeRle(reader(hex), true);
        assertThat(toArray(actual)).isEqualTo(expected);
    }

    @Test
    void testDecodeRleDarkAgesShorts() throws IOException {
        var hex = """
            FA 80 01 34 00 02 52 00 01 56 00 01 58 00 05 5A
            00 04 62 00 01 69 00 02 6C 00 01 79 00 02 83 00
            02 87 00 02 8B 00 04 8E 00 02 99 00 06 A3 00 1A
            D4 00 02 EF 00 14 F3 00 07 09 01 16 11 01 01 28
            01 06 2A 01 0A 31 01 01 3C 01 01 3E 01 03 40 01
            03 44 01 02 48 01 01 4B 01 01 4E 01 03 50 01 07
            54 01 02 5D 01 08 60 01 01 6A 01 04 6C 01 06 71
            01 01 79 01 02 7B 01 06 7E 01 01 85 01 01 8C 01
            02 91 01 02 96 01 01 9F 01 01 AA 01 01 B3 01 01
            B9 01 03 BB 01 02 BF 01 01 C3 01 01 C5 01 01 CA
            01 06 CC 01 05 DB 01 01 E5 01 01 EE 01 01 F2 01
            02 F4 01 02 F8 01 02 09 02 01 10 02 02 17 02 04
            1C 02 03 25 02 01 2B 02 04 2E 02 04 33 02 02 38
            02 03 3B 02 01 3F 02 03 41 02 01 46 02 01 48 02
            """;

        var expected = new int[]{
            +52, +82, +83, +86, +88, +90, +91, +92, +93, +94, +98, +99, 100, 101, 105, 108,
            109, 121, 131, 132, 135, 136, 139, 140, 142, 143, 144, 145, 153, 154, 163, 164,
            165, 166, 167, 168, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223,
            224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 239, 240,
            243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258,
            259, 260, 261, 262, 265, 266, 267, 268, 269, 270, 271, 273, 274, 275, 276, 277,
            278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293,
            294, 296, 298, 299, 300, 301, 302, 303, 305, 306, 307, 308, 309, 310, 311, 312,
            313, 314, 316, 318, 320, 321, 322, 324, 325, 326, 328, 329, 331, 334, 336, 337,
            338, 340, 341, 342, 343, 344, 345, 346, 349, 350, 352, 353, 354, 355, 356, 357,
            358, 359, 362, 364, 365, 366, 367, 369, 370, 371, 372, 373, 374, 377, 379, 380,
            382, 383, 384, 385, 386, 387, 389, 396, 401, 402, 406, 407, 415, 426, 435, 441,
            443, 444, 445, 447, 448, 451, 453, 458, 460, 461, 462, 463, 464, 465, 475, 476,
            477, 478, 479, 485, 494, 498, 500, 501, 504, 505, 521, 522, 528, 535, 536, 540,
            541, 542, 543, 549, 550, 551, 555, 558, 559, 560, 561, 563, 564, 565, 566, 568,
            569, 571, 572, 573, 575, 577, 578, 579, 582, 584
        };
        var actual = Md6AnimDecoder.decodeRle(reader(hex), true);
        assertThat(toArray(actual)).isEqualTo(expected);
    }

    @Test
    void testDecodeRleShortHeaderMissing() throws IOException {
        var hex = "03 00 81 02 07";

        var actual = Md6AnimDecoder.decodeRle(reader(hex), true);
        assertThat(toArray(actual)).containsExactly(MISS, 7, 8);
    }

    @Test
    void testDecodeQuatIdentity() throws IOException {
        var mid = Math.round(Math.sqrt(0.5) / (Math.sqrt(2) / 0x7FFF));
        var hex = "%04X".formatted(mid).repeat(3);

        var quat = Md6AnimDecoder.decodeQuat(reader(hex).order(ByteOrder.BIG_ENDIAN));

        assertThat(quat.x()).isCloseTo(0.0f, within(1e-4f));
        assertThat(quat.y()).isCloseTo(0.0f, within(1e-4f));
        assertThat(quat.z()).isCloseTo(0.0f, within(1e-4f));
        assertThat(quat.w()).isCloseTo(1.0f, within(1e-4f));
    }

    private static BinaryReader reader(String hex) {
        var bytes = HexFormat.of().parseHex(hex.replaceAll("\\s+", ""));
        return BinaryReader.wrap(Bytes.wrap(bytes)).order(ByteOrder.LITTLE_ENDIAN);
    }

    private static int[] toArray(Ints ints) {
        var result = new int[ints.length()];
        for (var i = 0; i < result.length; i++) {
            result[i] = ints.get(i);
        }
        return result;
    }
}
