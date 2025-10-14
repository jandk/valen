package be.twofold.valen.game.dyinglight.reader.rpack;

import be.twofold.valen.core.util.*;

public enum ResourceType implements ValueEnum<Integer> {
    Invalid(0x0),
    Mesh(0x10),
    MeshFixups(0x11),
    Skin(0x12),
    Model(0x18),
    Texture(0x20),
    TextureBitmapData(0x21),
    TextureMipBitmapData(0x22),
    Material(0x30),
    Shader(0x31),
    Animation(0x40),
    AnimationStream(0x41),
    AnimationScr(0x42),
    AnimationScrFixups(0x43),
    AnimMetadata(0x44),
    AnimPayload(0x45),
    AnimFallback(0x46),
    AnimGraphBank(0x47),
    AnimGraphBankFixups(0x48),
    AnimCustomResource(0x49),
    AnimCustomResourceFixups(0x4A),
    GpuFx(0x51),
    EnvprobeBin(0x55),
    VoxelizerBin(0x56),
    Area(0x5A),
    PrefabText(0x60),
    Prefab(0x61),
    PrefabFixups(0x62),
    Sound(0x65),
    SoundMusic(0x66),
    SoundSpeech(0x67),
    SoundStream(0x68),
    SoundLocal(0x69),
    VertexData(0xF0),
    IndexData(0xF1),
    GeometryData(0xF2),
    ClothData(0xF3),
    TinyObjects(0xF8),
    BuilderInformation(0xFF);

    private final int value;

    ResourceType(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
