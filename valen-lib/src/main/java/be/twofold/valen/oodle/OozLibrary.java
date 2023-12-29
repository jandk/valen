package be.twofold.valen.oodle;

import com.sun.jna.*;

public interface OozLibrary extends Library {

    int Kraken_Compress(byte[] src, int srcLength, byte[] dst, int level);

    int Kraken_Decompress(byte[] src, int srcLength, byte[] dst, int dstLength);

}
