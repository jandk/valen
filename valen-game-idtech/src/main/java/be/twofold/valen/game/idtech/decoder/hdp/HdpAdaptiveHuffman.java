package be.twofold.valen.game.idtech.decoder.hdp;

import wtf.reversed.toolbox.io.*;

import java.io.*;

/**
 * Variable-length decoder that switches between table variants as the symbols it decodes drift.
 */
final class HdpAdaptiveHuffman {
    private static final short[][] g4HuffLookupTable = {{
        19, 19, 19, 19, 27, 27, 27, 27, 10, 10, 10, 10, 10, 10, 10, 10,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0
    }};
    private static final short[][] g5HuffLookupTable = {{
        28, 28, 36, 36, 19, 19, 19, 19, 10, 10, 10, 10, 10, 10, 10, 10,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, {
        11, 11, 11, 11, 19, 19, 19, 19, 27, 27, 27, 27, 35, 35, 35, 35,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }};
    private static final short[][] g6HuffLookupTable = {{
        13, 29, 44, 44, 19, 19, 19, 19, 34, 34, 34, 34, 34, 34, 34, 34,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, {
        12, 12, 28, 28, 43, 43, 43, 43, 2, 2, 2, 2, 2, 2, 2, 2,
        18, 18, 18, 18, 18, 18, 18, 18, 34, 34, 34, 34, 34, 34, 34, 34,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, {
        4, 4, 12, 12, 43, 43, 43, 43, 18, 18, 18, 18, 18, 18, 18, 18,
        26, 26, 26, 26, 26, 26, 26, 26, 34, 34, 34, 34, 34, 34, 34, 34,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, {
        5, 13, 36, 36, 43, 43, 43, 43, 18, 18, 18, 18, 18, 18, 18, 18,
        25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }};
    private static final short[][] g7HuffLookupTable = {{
        45, 53, 36, 36, 27, 27, 27, 27, 2, 2, 2, 2, 2, 2, 2, 2,
        10, 10, 10, 10, 10, 10, 10, 10, 18, 18, 18, 18, 18, 18, 18, 18,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, {
        -32736, 37, 28, 28, 19, 19, 19, 19, 10, 10, 10, 10, 10, 10, 10, 10,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }};
    private static final short[][] g8HuffLookupTable = {{
        53, 21, 28, 28, 11, 11, 11, 11, 43, 43, 43, 43, 59, 59, 59, 59,
        2, 2, 2, 2, 2, 2, 2, 2, 34, 34, 34, 34, 34, 34, 34, 34,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }, {
        53, 21, 28, 28, 11, 11, 11, 11, 43, 43, 43, 43, 59, 59, 59, 59,
        2, 2, 2, 2, 2, 2, 2, 2, 34, 34, 34, 34, 34, 34, 34, 34,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }};
    private static final short[][] g9HuffLookupTable = {{
        13, 29, 37, 61, 20, 20, 68, 68, 3, 3, 3, 3, 51, 51, 51, 51,
        41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0
    }, {
        -32736, 53, 28, 28, 11, 11, 11, 11, 19, 19, 19, 19, 43, 43, 43, 43,
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        -32734, 4, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0
    }};
    private static final short[][] g12HuffLookupTable = {{
        -32736, 5, 76, 76, 37, 53, 69, 85, 43, 43, 43, 43, 91, 91, 91, 91,
        57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57,
        -32734, 1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    }, {
        -32736, 85, 13, 53, 4, 4, 36, 36, 43, 43, 43, 43, 67, 67, 67, 67,
        75, 75, 75, 75, 91, 91, 91, 91, 58, 58, 58, 58, 58, 58, 58, 58,
        2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    }, {
        -32736, 37, 92, 92, 11, 11, 11, 11, 43, 43, 43, 43, 59, 59, 59, 59,
        67, 67, 67, 67, 75, 75, 75, 75, 2, 2, 2, 2, 2, 2, 2, 2,
        -32734, -32732, 2, 3, 6, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    }, {
        -32736, 29, 37, 69, 3, 3, 3, 3, 43, 43, 43, 43, 59, 59, 59, 59,
        75, 75, 75, 75, 91, 91, 91, 91, 10, 10, 10, 10, 10, 10, 10, 10,
        -32734, 10, 2, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    }, {
        -32736, 93, 28, 28, 60, 60, 76, 76, 3, 3, 3, 3, 43, 43, 43, 43,
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
        -32734, -32732, -32730, 2, 4, 8, 6, 10, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    }};

    // @formatter:off
    private static final int[][] g5DeltaTable = {
        {  0, -1,  0,  1,  1 },
    };
    private static final int[][] g6DeltaTable = {
        { -1,  1,  1,  1,  0,  1 },
        { -2,  0,  0,  2,  0,  0 },
        { -1, -1,  0,  1, -2,  0 },
    };
    private static final int[][] g7DeltaTable = {
        {  1,  0, -1, -1, -1, -1, -1 },
    };
    private static final int[][] g8DeltaTable = {
        { -1,  0,  1,  1, -1,  0,  1,  1 },
    };
    private static final int[][] g9DeltaTable = {
        {  2,  2,  1,  1, -1, -2, -2, -2, -3 },
    };
    private static final int[][] g12DeltaTable = {
        {  1,  1,  1,  1,  1,  0,  0, -1,  2,  1,  0,  0 },
        {  2,  2, -1, -1, -1,  0, -2, -1,  0,  0, -2, -1 },
        { -1,  1,  0,  2,  0,  0,  0,  0, -2,  0,  1,  1 },
        {  0,  1,  0,  1, -2,  0, -1, -1, -2, -1, -2, -2 },
    };
    // @formatter:on

    private final short[][] lut;
    private final int[][] delta;          // null for N=4
    private final boolean useSecondDisc;

    private short[] decTable;
    private int[] delta0;
    private int[] delta1;

    private int tableIndex;
    private int discriminant;
    private int discriminant1;
    private boolean isInitialized;

    HdpAdaptiveHuffman(int numSymbols) {
        this.lut = lutFor(numSymbols);
        this.delta = deltaFor(numSymbols);
        this.useSecondDisc = delta != null && delta.length > 1;
        adaptDiscriminant();
    }

    static HdpAdaptiveHuffman[] allocate(int... alphabet) {
        var huffmans = new HdpAdaptiveHuffman[alphabet.length];
        for (int i = 0; i < alphabet.length; i++) {
            huffmans[i] = new HdpAdaptiveHuffman(alphabet[i]);
        }
        return huffmans;
    }

    int decodeSymbol(BitSource bits) throws IOException {
        int peek = bits.peek(5);
        short entry = decTable[peek];
        int sym;
        if (entry >= 0) {
            // Short code: (symbol << 3) | length.
            bits.skip(entry & 7);
            sym = entry >> 3;
        } else {
            // Long code: commit the 5 peeked bits, walk the tree by bit.
            bits.skip(5);
            int result = entry;
            while (result < 0) {
                result = decTable[result + 0x8000 + bits.readOne()];
            }
            sym = result;
        }
        if (delta0 != null) {
            discriminant += delta0[sym];
        }
        if (delta1 != null) {
            discriminant1 += delta1[sym];
        }
        return sym;
    }

    // DEVIATION: shaped differently from hdpref, but functionally identical.
    void adaptDiscriminant() {
        if (!isInitialized) {
            isInitialized = true;
            discriminant = 0;
            discriminant1 = 0;
            tableIndex = useSecondDisc ? 1 : 0;
        } else {
            int d0 = discriminant;
            int d1 = discriminant1;
            int dRef = useSecondDisc ? d1 : d0;
            int lo = (tableIndex == 0) ? Integer.MIN_VALUE : -8;
            int hi = (tableIndex == lut.length - 1) ? 0x40000000 : 8;

            if (d0 < lo) {
                tableIndex--;
                d0 = 0;
                d1 = 0;
            } else if (dRef > hi) {
                tableIndex++;
                d0 = 0;
                d1 = 0;
            } else {
                d0 = Math.clamp(d0, -64, 64);
                d1 = Math.clamp(d1, -64, 64);
            }
            discriminant = d0;
            discriminant1 = d1;
        }

        bindTables();
    }

    /**
     * Picks tables for the current {@code tableIndex}; the clamps reuse the adjacent edge at either end.
     */
    private void bindTables() {
        int variantCount = lut.length;
        decTable = lut[tableIndex];
        delta0 = delta == null ? null : delta[Math.max(0, tableIndex - 1)];
        delta1 = useSecondDisc ? delta[Math.min(tableIndex, variantCount - 2)] : null;
    }

    private short[][] lutFor(int n) {
        return switch (n) {
            case 4 -> g4HuffLookupTable;
            case 5 -> g5HuffLookupTable;
            case 6 -> g6HuffLookupTable;
            case 7 -> g7HuffLookupTable;
            case 8 -> g8HuffLookupTable;
            case 9 -> g9HuffLookupTable;
            case 12 -> g12HuffLookupTable;
            default -> null;
        };
    }

    private int[][] deltaFor(int n) {
        return switch (n) {
            case 5 -> g5DeltaTable;
            case 6 -> g6DeltaTable;
            case 7 -> g7DeltaTable;
            case 8 -> g8DeltaTable;
            case 9 -> g9DeltaTable;
            case 12 -> g12DeltaTable;
            default -> null;
        };
    }
}
