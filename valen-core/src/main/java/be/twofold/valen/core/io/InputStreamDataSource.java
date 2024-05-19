package be.twofold.valen.core.io;

import java.io.*;
import java.util.*;

public final class InputStreamDataSource extends DataSource {
    private final InputStream stream;

    public InputStreamDataSource(InputStream stream) {
        this.stream = Objects.requireNonNull(stream);
    }

    @Override
    public byte readByte() throws IOException {
        int read = stream.read();
        if (read == -1) {
            throw new EOFException();
        }
        return (byte) read;
    }

    @Override
    public void readBytes(byte[] dst, int off, int len, boolean buffered) throws IOException {
        Objects.checkFromIndexSize(off, len, dst.length);

        while (len > 0) {
            int read = stream.read(dst, off, len);
            if (read == -1) {
                throw new EOFException();
            }
            off += read;
            len -= read;
        }
    }

    @Override
    public long tell() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void seek(long pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }
}
