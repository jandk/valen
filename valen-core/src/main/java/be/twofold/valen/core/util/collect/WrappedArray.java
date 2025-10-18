package be.twofold.valen.core.util.collect;

import java.nio.*;

public interface WrappedArray {
    int size();

    Buffer asBuffer();
}
