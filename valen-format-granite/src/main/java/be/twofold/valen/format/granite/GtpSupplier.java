package be.twofold.valen.format.granite;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public interface GtpSupplier {
    BinarySource open(String name) throws IOException;
}
