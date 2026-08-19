package be.twofold.valen.game.idtech.decoder.hdp.header;

import wtf.reversed.toolbox.io.*;

import java.io.*;

/**
 * The JPEG XR image plane header: colour format, sub-bands present, and the DC, LP and HP quantizers.
 */
public record HdpPlaneHeader(
    HdpColorFormat colorFormat,
    boolean scaledArith,
    HdpSubband subband,
    int numChannels,
    HdpQp dcQp,
    HdpQp lpQp,
    HdpQp hpQp
) {
    public static HdpPlaneHeader read(BitSource bits) throws IOException {
        HdpColorFormat colorFormat = HdpColorFormat.fromValue(bits.read(3));

        boolean scaledArith = bits.readFlag();
        if (!scaledArith) {
            throw new UnsupportedOperationException("scaledArith=false");
        }

        HdpSubband subband = HdpSubband.fromValue(bits.read(4));
        if (subband != HdpSubband.SB_NO_FLEXBITS) {
            throw new UnsupportedOperationException("subband=" + subband);
        }

        int numChannels = switch (colorFormat) {
            case Y_ONLY -> 1;
            case YUV_444 -> {
                int chromaCentering = bits.read(4);
                if (chromaCentering != 0) {
                    throw new UnsupportedOperationException("chromaCentering=" + chromaCentering);
                }
                int chromaInterpretation = bits.read(4);
                if (chromaInterpretation != 0) {
                    throw new UnsupportedOperationException("chromaInterpretation=" + chromaInterpretation);
                }
                yield 3;
            }
            case N_CHANNEL -> {
                int numChannelsMinus1 = bits.read(4);
                if (numChannelsMinus1 > 2) {
                    throw new UnsupportedOperationException("numChannelsMinus1=" + numChannelsMinus1);
                }

                int chromaInterpretation = bits.read(4);
                if (chromaInterpretation != 0) {
                    throw new UnsupportedOperationException("chromaInterpretation=" + chromaInterpretation);
                }
                yield numChannelsMinus1 + 1;
            }
            default -> throw new UnsupportedOperationException("colorFormat=" + colorFormat);
        };

        // All QPs are present
        HdpQp dcQp = readDcQp(bits, numChannels);
        HdpQp lpQp = readLpHpQp(bits, numChannels);
        HdpQp hpQp = readLpHpQp(bits, numChannels);

        bits.alignToByte();

        return new HdpPlaneHeader(
            colorFormat,
            scaledArith,
            subband,
            numChannels,
            dcQp,
            lpQp,
            hpQp
        );
    }

    private static HdpQp readDcQp(BitSource bits, int numChannels) throws IOException {
        boolean uniform = bits.readFlag();
        if (!uniform) {
            throw new UnsupportedOperationException("uniform=false");
        }
        return HdpQp.read(bits, numChannels);
    }

    private static HdpQp readLpHpQp(BitSource bits, int numChannels) throws IOException {
        boolean previous = bits.readFlag();
        if (previous) {
            throw new UnsupportedOperationException("previous=true");
        }
        return readDcQp(bits, numChannels);
    }
}
