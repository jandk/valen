package be.twofold.valen.format.granite.gdex;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;

public enum GdexItemTag implements ValueEnum<Integer> {
    ADDR(0x52444441),
    ATLS(0x534C5441),
    BDPR(0x52504442),
    BINF(0x464E4942),
    BLDV(0x56444C42),
    BLKS(0x534B4C42),
    CMPW(0x57504D43),
    COMP(0x504D4F43),
    DATE(0x45544144),
    HGHT(0x54484748),
    INDX(0x58444E49),
    INFO(0x4F464E49),
    LAYR(0x5259414C),
    LINF(0x464E494C),
    LTMP(0x504D544C),
    MAJR(0x524A414D),
    META(0x4154454D),
    MINR(0x524E494D),
    NAME(0x454D414E),
    PROJ(0x4A4F5250),
    SRGB(0x42475253),
    THMB(0x424D4854),
    TILE(0x454C4954),
    TXTR(0x52545854),
    TXTS(0x53545854),
    TYPE(0x45505954),
    WDTH(0x48544457),
    XXXX(0x58585858),
    YYYY(0x59595959),
    ;

    private final int value;

    GdexItemTag(int value) {
        this.value = value;
    }

    public static GdexItemTag read(BinaryReader reader) throws IOException {
        return ValueEnum.fromValue(GdexItemTag.class, reader.readInt());
    }

    @Override
    public Integer value() {
        return value;
    }
}
