package be.twofold.valen;

import java.util.*;

public interface StreamLoader {

    Optional<byte[]> load(long identity, int size);

    boolean exists(long identity);

}
