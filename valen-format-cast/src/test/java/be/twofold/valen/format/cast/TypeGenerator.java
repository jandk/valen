package be.twofold.valen.format.cast;

import java.util.*;

final class TypeGenerator {
    private static final RawType RootType = new RawType(
        "Root",
        "Model, Animation, Instance, Metadata",
        ""
    );
    private static final RawType ModelType = new RawType(
        "Model",
        "Skeleton, Mesh, Hair, Blend Shape, Material",
        """
            Name (n) 	String (s) 	False 	False
            Position (p) 	Vector 3 (v3) 	False 	False
            Rotation (r) 	Vector 4 (v4) 	False 	False
            Scale (s) 	Vector 3 (v3) 	False 	False
            """
    );
    private static final RawType MeshType = new RawType(
        "Mesh",
        "",
        """
            Name (n) 	String (s) 	False 	False
            Vertex Position Buffer (vp) 	Vector 3 (v3) 	True 	True
            Vertex Normal Buffer (vn) 	Vector 3 (v3) 	True 	False
            Vertex Tangent Buffer (vt) 	Vector 3 (v3) 	True 	False
            Vertex Color Buffer (c%d) 	Integer 32 (i), Vector 4 (v4) 	True 	False
            Vertex UV Buffer (u%d) 	Vector 2 (v2) 	True 	False
            Vertex Weight Bone Buffer (wb) 	Integer 32 (i), Short (h), Byte (b) 	True 	False
            Vertex Weight Value Buffer (wv) 	Float (f) 	True 	False
            Face Buffer (f) 	Integer 32 (i), Short (h), Byte (b) 	True 	True
            Color Layer Count (cl) 	Integer 32 (i), Short (h), Byte (b) 	False 	True if has color layers else False
            UV Layer Count (ul) 	Integer 32 (i), Short (h), Byte (b) 	False 	True if has uv layers else False
            Maximum Weight Influence (mi) 	Integer 32 (i), Short (h), Byte (b) 	False 	True if has weights else False
            Skinning Method (sm) 	String (s) [linear, quaternion] 	False 	False
            Material (Hash of CastNode:Material) (m) 	Integer 64 (l) 	False 	False
            """
    );
    private static final RawType HairType = new RawType(
        "Hair",
        "",
        """
            Name (n) 	String (s) 	False 	False
            Segments Buffer (se) 	Integer 32 (i), Short (h), Byte (b) 	True 	True
            Particle Buffer (pt) 	Vector 3 (v3) 	True 	True
            Material (Hash of CastNode:Material) (m) 	Integer 64 (l) 	False 	False
            """
    );
    private static final RawType BlendShapeType = new RawType(
        "Blend Shape",
        "",
        """
            Name (n) 	String (s) 	False 	True
            Base Shape (Hash of CastNode:Mesh) (b) 	Integer 64 (l) 	False 	True
            Target Shape Vertex Indices (vi) 	Byte (b), Short (h), Integer 32 (i) 	True 	True
            Target Shape Vertex Positions (vp) 	Vector 3 (v3) 	True 	True
            Target Weight Scale (ts) 	Float (f) 	True 	False
            """
    );
    private static final RawType SkeletonType = new RawType(
        "Skeleton",
        "Bone, IK Handle, Constraint",
        """
            """
    );
    private static final RawType BoneType = new RawType(
        "Bone",
        "",
        """
            Name (n) 	String (s) 	False 	True
            Parent Index (p) 	Integer 32 (i) 	False 	False
            Segment Scale Compensate (ssc) 	Byte (b) [True, False] 	False 	False
            Local Position (lp) 	Vector 3 (v3) 	False 	False
            Local Rotation (lr) 	Vector 4 (v4) 	False 	False
            World Position (wp) 	Vector 3 (v3) 	False 	False
            World Rotation (wr) 	Vector 4 (v4) 	False 	False
            Scale (s) 	Vector 3 (v3) 	False 	False
            """
    );
    private static final RawType IKHandleType = new RawType(
        "IK Handle",
        "",
        """
            Name (n) 	String (s) 	False 	False
            Start Bone Hash (sb) 	Integer 64 (l) 	False 	True
            End Bone Hash (eb) 	Integer 64 (l) 	False 	True
            Target Bone Hash (tb) 	Integer 64 (l) 	False 	False
            Pole Vector Bone Hash (pv) 	Integer 64 (l) 	False 	False
            Pole Bone Hash (pb) 	Integer 64 (l) 	False 	False
            Use Target Rotation (tr) 	Byte (b) [True, False] 	False 	False
            """
    );
    private static final RawType ConstraintType = new RawType(
        "Constraint",
        "",
        """
            Name (n) 	String (s) 	False 	False
            Constraint Type (ct) 	String (s) [pt, or, sc] 	False 	True
            Constraint Bone Hash (cb) 	Integer 64 (l) 	False 	True
            Target Bone Hash (tb) 	Integer 64 (l) 	False 	True
            Maintain Offset (mo) 	Byte (b) [True, False] 	False 	False
            Custom Offset (co) 	Vector3 (v3), Vector 4 (v4) 	False 	False
            Weight (wt) 	Float (f) 	False 	False
            Skip X (sx) 	Byte (b) [True, False] 	False 	False
            Skip Y (sy) 	Byte (b) [True, False] 	False 	False
            Skip Z (sz) 	Byte (b) [True, False] 	False 	False
            """
    );
    private static final RawType MaterialType = new RawType(
        "Material",
        "File, Color",
        """
            Name (n) 	String (s) 	False 	True
            Type (t) 	String (s) [pbr] 	False 	True
            Albedo Hash (albedo) 	Integer 64 (l) 	False 	False
            Diffuse Hash (diffuse) 	Integer 64 (l) 	False 	False
            Normal Hash (normal) 	Integer 64 (l) 	False 	False
            Specular Hash (specular) 	Integer 64 (l) 	False 	False
            Gloss Hash (gloss) 	Integer 64 (l) 	False 	False
            Roughness Hash (roughness) 	Integer 64 (l) 	False 	False
            Emissive Hash (emissive) 	Integer 64 (l) 	False 	False
            Emissive Mask Hash (emask) 	Integer 64 (l) 	False 	False
            Ambient Occlusion Hash (ao) 	Integer 64 (l) 	False 	False
            Cavity Hash (cavity) 	Integer 64 (l) 	False 	False
            Anisotropy Hash (aniso) 	Integer 64 (l) 	False 	False
            Extra (x) Hash (extra%d) 	Integer 64 (l) 	False 	False
            """
    );
    private static final RawType FileType = new RawType(
        "File",
        "",
        """
            Path (p) 	String (s) 	False 	True
            """
    );
    private static final RawType ColorType = new RawType(
        "Color",
        "",
        """
            Name (n) 	String (s) 	False 	False
            Color Space (cs) 	String (s) [srgb, linear] 	False 	False
            Rgba Color (rgba) 	Vector 4 (v4) 	False 	True
            """
    );
    private static final RawType AnimationType = new RawType(
        "Animation",
        "Skeleton, Curve, Curve Mode Override, Notification Track",
        """
            Name (n) 	String (s) 	False 	False
            Framerate (fr) 	Float (f) 	False 	True
            Looping (lo) 	Byte (b) [True, False] 	False 	False
            """
    );
    private static final RawType CurveType = new RawType(
        "Curve",
        "",
        """
            Node Name (nn) 	String (s) 	False 	True
            Key Property Name (kp) 	String (s) [rq, tx, ty, tz, sx, sy, sz, bs, vb] 	False 	True
            Key Frame Buffer (kb) 	Byte (b), Short (h), Integer 32 (i) 	True 	True
            Key Value Buffer (kv) 	Byte (b), Short (h), Integer 32 (i), Float (f), Vector 4 (v4) 	True 	True
            Mode (m) 	String (s) [additive, absolute, relative] 	False 	True
            Additive Blend Weight (ab) 	Float (f) 	False 	False
            """
    );
    private static final RawType CurveModeOverrideType = new RawType(
        "Curve Mode Override",
        "",
        """
            Node Name (nn) 	String (s) 	False 	True
            Mode (m) 	String (s) [additive, absolute, relative] 	False 	True
            Override Translation Curves (ot) 	Byte (b) [True, False] 	False 	False
            Override Rotation Curves (or) 	Byte (b) [True, False] 	False 	False
            Override Scale Curves (os) 	Byte (b) [True, False] 	False 	False
            """
    );
    private static final RawType NotificationTrackType = new RawType(
        "Notification Track",
        "",
        """
            Name (n) 	String (s) 	False 	True
            Key Frame Buffer (kb) 	Byte (b), Short (h), Integer 32 (i) 	True 	True
            """
    );
    private static final RawType InstanceType = new RawType(
        "Instance",
        "File",
        """
            Name (n) 	String (s) 	False 	False
            Reference File (Hash of CastNode:File) (rf) 	Integer 64 (l) 	False 	True
            Position (p) 	Vector 3 (v3) 	False 	True
            Rotation (r) 	Vector 4 (v4) 	False 	True
            Scale (s) 	Vector 3 (v3) 	False 	True
            """
    );
    private static final RawType MetadataType = new RawType(
        "Metadata",
        "",
        """
            Author (a) 	String (s) 	False 	False
            Software (s) 	String (s) 	False 	False
            Up Axis (up) 	String (s) [x, y, z] 	False 	False
            """
    );

    private static final List<RawType> RawTypes = List.of(
        RootType,
        ModelType,
        MeshType,
        HairType,
        BlendShapeType,
        SkeletonType,
        BoneType,
        IKHandleType,
        ConstraintType,
        AnimationType,
        CurveType,
        CurveModeOverrideType,
        NotificationTrackType,
        MaterialType,
        FileType,
        ColorType,
        InstanceType,
        MetadataType
    );

    public static void main(String[] args) {
        List<TypeDef> types = RawTypes.stream().map(TypeParser::parse).toList();
        TypeClassWriter.generate(types);
    }
}
