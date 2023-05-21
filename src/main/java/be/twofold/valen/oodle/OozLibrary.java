package be.twofold.valen.oodle;

import com.sun.jna.*;

public interface OozLibrary extends Library {

    int SafeSpace = 64;

    int Kraken_Decompress(byte[] src, long src_len, byte[] dst, long dst_len);

}
