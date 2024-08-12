package be.twofold.valen.reader.havokshape;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

public final class HkTagFileReader {
    private String sdkVersion;
    private ByteBuffer data;

    private List<String> typeStrings;
    private List<HavokType> types;
    private List<String> fieldStrings;
    private List<HavokItem> items;

    public HkTagFileReader(ByteBuffer buffer) {
        readChunks(buffer, 0);

        OptionalInt max = items.stream()
            .mapToInt(item -> {
                var offset = item.offset();
                var count = item.count();
                var size = item.type().getSize();
                return offset + count * size;
            })
            .max();

        System.out.println("Max: " + max.orElse(-1));

        System.out.println("SDK Version: " + sdkVersion);
    }

    public void readChunks(ByteBuffer buffer, int indent) {
        var tag = HkTag.read(buffer);
        System.out.println(" ".repeat(indent * 2) + tag.type() + " " + tag.size() + " " + tag.flags());

        var slice = buffer.slice(buffer.position(), tag.sizeWithoutHeader());
        switch (tag.flags()) {
            case 0 -> {
                while (slice.hasRemaining()) {
                    readChunks(slice, indent + 1);
                }
            }
            case 1 -> readChunk(slice, tag);
            default -> throw new UnsupportedOperationException("Unsupported flags: " + tag.flags());
        }
        buffer.position(buffer.position() + tag.sizeWithoutHeader());
    }

    private void readChunk(ByteBuffer buffer, HkTag tag) {
        switch (tag.type()) {
            case TAG0, TYPE, INDX -> {
                throw new UnsupportedOperationException("Unsupported chunk: " + tag.type());
            }
            case SDKV -> {
                var bytes = new byte[tag.sizeWithoutHeader()];
                buffer.get(bytes);
                sdkVersion = new String(bytes, StandardCharsets.UTF_8);
            }
            case DATA -> data = buffer.slice(buffer.position(), tag.sizeWithoutHeader());
            case TPTR, TPAD, PTCH -> {
                // Skip for now
            }
            case TSTR -> typeStrings = readStrings(buffer);
            case TNA1 -> readTypeNames(buffer);
            case FSTR -> fieldStrings = readStrings(buffer);
            case TBDY -> readTypeFields(buffer);
            case THSH -> readTypeHashes(buffer);
            case ITEM -> readIndexItems(buffer);
        }

        assert !buffer.hasRemaining() : "Buffer has remaining bytes: " + buffer.remaining();
    }

    private List<String> readStrings(ByteBuffer buffer) {
        var bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        var string = new String(bytes, StandardCharsets.UTF_8);
        return List.of(string.split("\0"));
    }

    private void readTypeNames(ByteBuffer buffer) {
        var count = readPacked(buffer);
        types = IntStream.rangeClosed(0, count)
            .mapToObj(i -> new HavokType())
            .toList();

        for (var i = 1; i < count; i++) {
            readTypeName(types.get(i), buffer);
        }
    }

    private void readTypeName(HavokType type, ByteBuffer buffer) {
        var name = typeStrings.get(readPacked(buffer));
        var templateParams = readTemplateParams(buffer);

        type.setName(name);
        type.setTemplateParams(templateParams);
    }

    private List<HavokTypeTemplateParam> readTemplateParams(ByteBuffer buffer) {
        var count = readPacked(buffer);
        return IntStream.range(0, count)
            .mapToObj(__ -> readTemplateParam(buffer))
            .toList();
    }

    private HavokTypeTemplateParam readTemplateParam(ByteBuffer buffer) {
        var name = typeStrings.get(readPacked(buffer));
        var value = readPacked(buffer);

        return switch (name.charAt(0)) {
            case 't' -> new HavokTypeTemplateParam.TypeValue(name.substring(1), types.get(value));
            case 'v' -> new HavokTypeTemplateParam.IntValue(name.substring(1), value);
            default -> throw new UnsupportedOperationException();
        };
    }

    private void readTypeFields(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            updateType(buffer);
        }
    }

    private void updateType(ByteBuffer buffer) {
        var typeIndex = readPacked(buffer);
        if (typeIndex == 0) {
            return;
        }

        var type = types.get(typeIndex);
        type.setParent(types.get(readPacked(buffer)));
        var options = HkOption.fromCode(readPacked(buffer));
        type.setOptions(options);

        if (options.contains(HkOption.Format)) {
            type.setFormat(readPacked(buffer));
        }
        if (options.contains(HkOption.SubType)) {
            type.setSubType(types.get(readPacked(buffer)));
        }
        if (options.contains(HkOption.Version)) {
            type.setVersion(readPacked(buffer));
        }
        if (options.contains(HkOption.SizeAlign)) {
            type.setSize(readPacked(buffer));
            type.setAlign(readPacked(buffer));
        }
        if (options.contains(HkOption.Flags)) {
            type.setFlags(readPacked(buffer));
        }
        if (options.contains(HkOption.Fields)) {
            type.setFields(readFields(buffer));
        }
        if (options.contains(HkOption.Interfaces)) {
            type.setInterfaces(readInterfaces(buffer));
        }
        if (options.contains(HkOption.Attribute)) {
            type.setAttribute(readPacked(buffer));
        }
    }

    private List<HavokField> readFields(ByteBuffer buffer) {
        return IntStream.range(0, readPacked(buffer))
            .mapToObj(__ -> new HavokField(
                fieldStrings.get(readPacked(buffer)),
                readPacked(buffer),
                readPacked(buffer),
                types.get(readPacked(buffer))
            ))
            .toList();
    }

    private List<HavokInterface> readInterfaces(ByteBuffer buffer) {
        return IntStream.range(0, readPacked(buffer))
            .mapToObj(__ -> new HavokInterface(
                types.get(readPacked(buffer)),
                fieldStrings.get(readPacked(buffer))))
            .toList();
    }

    private void readTypeHashes(ByteBuffer buffer) {
        var count = readPacked(buffer);
        for (int i = 0; i < count; i++) {
            var index = readPacked(buffer);
            var hash = buffer.getInt();
            types.get(index).setHash(hash);
        }
    }

    private void readIndexItems(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        items = new ArrayList<>();
        while (buffer.hasRemaining()) {
            items.add(readIndexItem(buffer));
        }
    }

    private HavokItem readIndexItem(ByteBuffer buffer) {
        int typeFlags = buffer.getInt();
        HavokType type = types.get(typeFlags & 0xffffff);
        int flags = typeFlags >>> 24;
        int offset = buffer.getInt();
        int count = buffer.getInt();
        return new HavokItem(type, flags, offset, count);
    }

    private int readPacked(ByteBuffer buffer) {
        var b0 = Byte.toUnsignedInt(buffer.get());
        if ((b0 & 0x80) == 0) {
            return b0;
        }

        switch (b0 >>> 3) {
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            case 0x17: {
                var b1 = Byte.toUnsignedInt(buffer.get());
                return (b0 << 8 | b1) & 0x3fff;
            }

            case 0x18:
            case 0x19:
            case 0x1A:
            case 0x1B: {
                var b1 = Byte.toUnsignedInt(buffer.get());
                var b2 = Byte.toUnsignedInt(buffer.get());
                return (b0 << 16 | b1 << 8 | b2) & 0x1fffff;
            }
        }
        if ((b0 & 0xc0) == 0x80) {
            return (b0 & 0x3f) << +8 | Byte.toUnsignedInt(buffer.get());
        } else if ((b0 & 0xe0) == 0xc0) {
            return (b0 & 0x1f) << 16 | Byte.toUnsignedInt(buffer.get()) << +8 | Byte.toUnsignedInt(buffer.get());
        } else if ((b0 & 0xf0) == 0xe0) {
            return (b0 & 0x0f) << 24 | Byte.toUnsignedInt(buffer.get()) << 16 | Byte.toUnsignedInt(buffer.get()) << +8 | Byte.toUnsignedInt(buffer.get());
        } else {
            throw new UnsupportedOperationException("Unsupported packed value: " + b0);
        }
    }
}
