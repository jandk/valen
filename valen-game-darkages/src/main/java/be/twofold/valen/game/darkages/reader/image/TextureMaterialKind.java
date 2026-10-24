package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public enum TextureMaterialKind implements MaterialKind {
    TMK_NONE(0, null, null, false),
    TMK_ALBEDO(1, "albedo", "albedo", true),
    TMK_SPECULAR(2, "specular", "specular", true),
    TMK_NORMAL(3, "normal", "normal", true),
    TMK_SMOOTHNESS(4, "smoothness", "smoothness", true),
    TMK_COVER(5, "cover", "cover", true),
    TMK_SSSMASK(6, "sssmask", "sssmask", true),
    TMK_COLORMASK(7, "colormask", "colormask", true),
    TMK_BLOOMMASK(8, "bloommask", "bloommask", false),
    TMK_HEIGHTMAP(9, "heightmap", "heightmap", false),
    TMK_DECALALBEDO(10, "decalalbedo", "decalalbedo", false),
    TMK_DECALNORMAL(11, "decalnormal", "decalnormal", false),
    TMK_DECALSPECULAR(12, "decalspecular", "decalspecular", false),
    TMK_LIGHTPROJECT(13, "lightproject", "lightproject", false),
    TMK_PARTICLE(14, "particle", "particle", false),
    TMK_DECALHEIGHTMAP(15, "decalheightmap", "decalheight", false),
    TMK_AO(16, "ao", "ao", true),
    TMK_UNUSED_3(17, null, null, false),
    TMK_UI(18, "ui", "ui", false),
    TMK_FONT(19, "font", "font", false),
    TMK_LEGACY_FLASH_UI(20, "legacyflashui", "legacyflashui", false),
    TMK_UNUSED_4(21, null, null, false),
    TMK_BLENDMASK(22, "blendmask", "blendmask", true),
    TMK_PAINTEDDATAGRID(23, null, null, false),
    TMK_COUNT(24, null, null, false),
    ;

    private static final TextureMaterialKind[] VALUES = values();
    private final int value;
    private final String word;
    private final String suffix;
    private final boolean skipsFormat;

    TextureMaterialKind(int value, String word, String suffix, boolean skipsFormat) {
        this.value = value;
        this.word = word;
        this.suffix = suffix;
        this.skipsFormat = skipsFormat;
    }

    public static TextureMaterialKind read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(TextureMaterialKind.class, source.readInt());
    }

    public static Optional<TextureMaterialKind> parse(String word) {
        for (var kind : VALUES) {
            if (kind.word != null && kind.word.equalsIgnoreCase(word)) {
                return Optional.of(kind);
            }
        }
        return Optional.empty();
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public String suffix() {
        return suffix;
    }

    @Override
    public boolean skipsFormat(TextureFormat format) {
        return this == TMK_NORMAL
            ? format != TextureFormat.FMT_BC7
            : skipsFormat;
    }

    @Override
    public boolean isSmoothness() {
        return this == TMK_SMOOTHNESS;
    }
}
