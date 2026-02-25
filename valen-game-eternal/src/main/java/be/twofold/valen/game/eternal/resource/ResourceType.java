package be.twofold.valen.game.eternal.resource;

import wtf.reversed.toolbox.util.*;

public enum ResourceType implements ValueEnum<String> {
    AmbientSh("ambientsh"),
    Anim("anim"),
    BaseModel("baseModel"),
    BinaryFile("binaryFile"),
    BinaryGoreContainer("binaryGoreContainer"),
    BinaryMd6def("binarymd6def"),
    BinaryRig("binaryrig"),
    Cgr("cgr"),
    ColorLut("colorLUT"),
    CompFile("compfile"),
    CSwf("cswf"),
    DiscreteAnimation2("discreteanimation2"),
    File("file"),
    Font("font"),
    GeomCache("geomcache"),
    HavokRagDoll("havokragdoll"),
    HavokShape("havokShape"),
    HkNavMesh("hknavmesh"),
    HkNavMeshMediator("hknavmeshmediator"),
    HkNavVolume("hknavvolume"),
    HkNavVolumeMediator("hknavvolumemediator"),
    Image("image"),
    Json("json"),
    Layer("layer"),
    Material2("material2"),
    Model("model"),
    ModelStream("modelstream"),
    PrefetchGraph("prefetchgraph"),
    Pvs("pvs"),
    RenderParm("renderParm"),
    RenderProgDatabase("renderProgDatabase"),
    RenderProgResource("renderProgResource"),
    RsEmbSFile("rs_emb_sfile"),
    RsStreamFile("rs_streamfile"),
    Skeleton("skeleton"),
    StaticGeoStreamTree("staticGeoStreamTree"),
    StaticInstances("staticInstances"),
    StaticLightProbeStreamWorld("staticLightProbeStreamWorld"),
    StaticParticleModel("staticParticleModel"),
    StaticShadowGeom("staticShadowGeom"),
    StaticStreamTree("staticStreamTree"),
    UmbraSoundTome("umbrasoundtome"),
    UmbraTome("umbratome"),
    UmbraViewVolume("umbraviewvolume");

    private final String value;

    ResourceType(String value) {
        this.value = value;
    }

    public static ResourceType fromValue(String name) {
        return ValueEnum.fromValue(ResourceType.class, name);
    }

    @Override
    public String value() {
        return value;
    }
}
