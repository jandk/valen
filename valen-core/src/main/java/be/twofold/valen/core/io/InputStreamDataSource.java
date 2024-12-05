package be.twofold.valen.core.io;

import be.twofold.valen.core.util.*;

import java.io.*;

final class InputStreamDataSource extends DataSource {
    private final InputStream stream;

    InputStreamDataSource(InputStream stream) {
        this.stream = Check.notNull(stream);
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
        Check.fromIndexSize(off, len, dst.length);

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

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
