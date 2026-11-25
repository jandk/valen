package be.twofold.valen.core.audio.riff;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

sealed public interface RiffChunk
    permits RiffChunk.Container, RiffChunk.Data {

    FourCC id();

    int size();

    void write(BinaryWriter writer) throws IOException;

    static RiffChunk data(String id, Bytes payload) {
        return new Data(FourCC.parse(id), payload);
    }

    static RiffChunk container(String id, String type, RiffChunk... chunks) {
        return new Container(FourCC.parse(id), FourCC.parse(type), List.of(chunks));
    }

    default Data asData() {
        if (!(this instanceof Data)) {
            throw new IllegalStateException("Not a data chunk");
        }
        return (Data) this;
    }

    default Container asContainer() {
        if (!(this instanceof Container)) {
            throw new IllegalStateException("Not a container chunk");
        }
        return (Container) this;
    }

    record Data(
        FourCC id,
        Bytes payload,
        long declaredSize
    ) implements RiffChunk {
        public Data {
            Check.nonNull(id, "id");
            Check.nonNull(payload, "payload");
            Check.nonNegative(declaredSize, "declaredSize");
        }

        public Data(FourCC id, Bytes payload) {
            this(id, payload, payload.length());
        }

        @Override
        public int size() {
            return payload.length();
        }

        @Override
        public void write(BinaryWriter writer) throws IOException {
            id.write(writer);
            writer.writeInt(size());
            writer.writeBytes(payload);
            if ((size() & 1) != 0) {
                writer.writeZeros(1);
            }
        }

        @Override
        public String toString() {
            return "RiffChunk.Data(" +
                "id=" + id + ", " +
                "payload=" + payload + ", " +
                "declaredSize=" + declaredSize +
                ")";
        }
    }

    record Container(
        FourCC id,
        FourCC type,
        List<RiffChunk> chunks
    ) implements RiffChunk {
        public Container {
            Check.nonNull(id, "id");
            Check.nonNull(type, "type");
            Check.nonNull(chunks, "chunks");
        }

        @Override
        public int size() {
            var size = 4;
            for (var chunk : chunks) {
                var chunkSize = chunk.size();
                size += 8 + chunkSize + (chunkSize & 1);
            }
            return size;
        }

        @Override
        public void write(BinaryWriter writer) throws IOException {
            id.write(writer);
            writer.writeInt(size());
            type.write(writer);
            for (var chunk : chunks) {
                chunk.write(writer);
            }
        }

        public Optional<RiffChunk> findFirst(FourCC id) {
            for (var chunk : chunks) {
                if (chunk.id().equals(id)) {
                    return Optional.of(chunk);
                }
            }
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "RiffChunk.Container(" +
                "id=" + id + ", " +
                "type=" + type + ", " +
                "chunks=" + chunks +
                ")";
        }
    }
}
