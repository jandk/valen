package be.twofold.valen.core.util.collect;

import java.nio.*;

public interface Slice {
    int length();

    Buffer asBuffer();
}
