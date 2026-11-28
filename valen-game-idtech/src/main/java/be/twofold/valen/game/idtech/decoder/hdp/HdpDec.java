package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

/**
 * Drives one plane bitstream from its headers all the way to RGBA pixels.
 */
public final class HdpDec {
    private HdpDec() {
    }

    /**
     * Some, but not all, bitstreams have an extra CF_Y alpha plane.
     */
    public static byte[] decode(BitSource bits, int width, int height, boolean hasAlphaChannel) throws IOException {
        return postProcess(decodeSlots(bits, width, height, hasAlphaChannel), width, height);
    }

    /**
     * In two parts because the engine decodes up to here, so for testing.
     */
    static int[] decodeSlots(BitSource bits, int width, int height, boolean hasAlphaChannel) throws IOException {
        HdpPlaneHeader primaryHeader = HdpPlaneHeader.read(bits);
        HdpPlaneHeader alphaHeader = null;
        if (hasAlphaChannel) {
            alphaHeader = HdpPlaneHeader.read(bits);
            if (alphaHeader.colorFormat() != HdpColorFormat.Y_ONLY) {
                throw new IOException("Expected CF_Y_ONLY secondary plane, got " + alphaHeader.colorFormat());
            }
        }
        readIndexTable(bits);

        return decodePlanes(bits, primaryHeader, alphaHeader,
            Math.ceilDiv(width, 16), Math.ceilDiv(height, 16));
    }

    /**
     * Converts the engine output to actual RGBA bytes.
     */
    static byte[] postProcess(int[] slots, int width, int height) {
        int mbW = Math.ceilDiv(width, 16);
        int mbH = Math.ceilDiv(height, 16);
        int channelStride = mbH * mbW * HdpConstants.MB_SIZE;
        int totalChannels = slots.length / channelStride;

        short[] pixels = HdpPostProcess.unZigzag(slots, totalChannels, channelStride, mbW, mbH, width, height);
        return HdpPostProcess.toRgba(pixels, totalChannels, width, height);
    }

    /**
     * Planes share one bitstream: at each macroblock position the primary's bits are consumed, then the alpha's.
     */
    private static int[] decodePlanes(
        BitSource bits,
        HdpPlaneHeader primaryHeader,
        HdpPlaneHeader alphaHeader,
        int mbW, int mbH
    ) throws IOException {
        readTileHeader(bits);

        int totalChannels = primaryHeader.numChannels() +
            (alphaHeader != null ? alphaHeader.numChannels() : 0);

        int rowSize = mbW * HdpConstants.MB_SIZE;
        int rowBufsChannelStride = 2 * rowSize;
        int accumChannelStride = mbH * rowSize;
        int[] accum = new int[totalChannels * accumChannelStride];
        int[] bases = {0, rowSize};
        int[] rowBufs = new int[totalChannels * rowBufsChannelStride];

        HdpCodec firstCodec = new HdpCodec(primaryHeader, mbW, 0);
        if (alphaHeader != null) {
            firstCodec.nextCodec = new HdpCodec(alphaHeader, mbW, primaryHeader.numChannels());
        }

        int lastMbY = -1;

        for (int mbY = 0; mbY <= mbH; mbY++) {
            if (mbY != lastMbY) {
                if (lastMbY >= 0) {
                    int finalizedRow = lastMbY - 1;
                    if (finalizedRow >= 0) {
                        for (int ch = 0; ch < totalChannels; ch++) {
                            System.arraycopy(
                                rowBufs, ch * rowBufsChannelStride + bases[0],
                                accum, ch * accumChannelStride + finalizedRow * rowSize,
                                rowSize);
                        }
                    }
                    int tmp = bases[0];
                    bases[0] = bases[1];
                    bases[1] = tmp;
                }
                lastMbY = mbY;
                for (int ch = 0; ch < totalChannels; ch++) {
                    int chBase = ch * rowBufsChannelStride;
                    Arrays.fill(rowBufs, chBase + bases[1], chBase + bases[1] + rowSize, 0);
                }
            }

            for (int mbX = 0; mbX <= mbW; mbX++) {
                boolean sentinel = (mbX == mbW || mbY == mbH);
                for (var codec = firstCodec; codec != null; codec = codec.nextCodec) {
                    codec.updateTilePos(mbX, mbY);
                    if (!sentinel) {
                        codec.decodeMacroblock(bits, bases[1], rowBufs);
                    }
                }
                HdpTransformInv.invTransformMacroblock(rowBufs,
                    totalChannels, rowBufsChannelStride,
                    bases[0], bases[1],
                    mbX, mbW, mbY, mbH);
            }
            if (mbY < mbH) {
                for (var codec = firstCodec; codec != null; codec = codec.nextCodec) {
                    codec.advanceRow();
                }
            }
        }
        for (int ch = 0; ch < totalChannels; ch++) {
            System.arraycopy(
                rowBufs, ch * rowBufsChannelStride + bases[0],
                accum, ch * accumChannelStride + (mbH - 1) * rowSize,
                rowSize);
        }

        return accum;
    }

    /**
     * Skip the tile index table, which id-Tech always emits empty.
     */
    private static void readIndexTable(BitSource bits) throws IOException {
        long skipBytes = getVLWordEsc(bits);
        if (skipBytes != 0) {
            throw new UnsupportedOperationException("skipBytes=" + skipBytes);
        }
    }

    private static long getVLWordEsc(BitSource bits) throws IOException {
        long firstByte = bits.readLong(8);
        if (firstByte < 0xFB) {
            long secondByte = bits.readLong(8);
            return firstByte << 8 | secondByte;
        } else if (firstByte == 0xFB) {
            return bits.readLong(32);
        } else if (firstByte == 0xFC) {
            long hi = bits.readLong(32);
            long lo = bits.readLong(32);
            return (hi << 32) | lo;
        } else {
            return 0;
        }
    }

    private static void readTileHeader(BitSource bits) throws IOException {
        int tileStartCode = bits.read(24);
        if (tileStartCode != 0x00_0001) {
            throw new IOException("Invalid tile start code: " + tileStartCode);
        }

        int tileLocationHash = bits.read(5);
        if (tileLocationHash != 0) {
            throw new UnsupportedOperationException("tileLocationHash=" + tileLocationHash);
        }
        int tileType = bits.read(3);
        if (tileType != 0) {
            throw new UnsupportedOperationException("tileType=" + tileType);
        }

        bits.alignToByte();
    }
}
