package be.twofold.valen.resource;

import java.util.*;

public enum ResourceType {
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
    Material2("material2"),
    Model("model"),
    ModelStream("modelstream"),
    PrefetchGraph("prefetchgraph"),
    Pvs("pvs"),
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

    private static final ResourceType[] VALUES = values();
    private final String name;

    ResourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ResourceType fromName(String name) {
        return Arrays.stream(VALUES)
            .filter(type -> type.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown resource type: " + name));
    }
}
