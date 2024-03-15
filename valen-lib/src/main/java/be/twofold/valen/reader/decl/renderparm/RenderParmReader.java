package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.util.*;

public final class RenderParmReader implements ResourceReader<RenderParm> {
    @Inject
    public RenderParmReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.RsStreamFile
               && entry.nameString().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(BetterBuffer buffer, Resource resource) {
        var bytes = buffer.getBytes(buffer.length());
        DeclParser parser = new DeclParser(new String(bytes));

        parser.expect(DeclTokenType.OpenBrace);
        String name = parser.expectName().toLowerCase(Locale.ROOT);

        return switch (name) {
            case "accelerationstructure" -> new RenderParm();
            case "bool" -> new RenderParm();
            case "colorlut" -> new RenderParm();
            case "f32" -> new RenderParm();
            case "f32vec2" -> new RenderParm();
            case "f32vec3" -> new RenderParm();
            case "f32vec4" -> new RenderParm();
            case "imagebuffer2d" -> new RenderParm();
            case "imagebuffer3d" -> new RenderParm();
            case "imagestorebuffer2d" -> new RenderParm();
            case "imagestorebuffer3d" -> new RenderParm();
            case "program" -> new RenderParm();
            case "sampler" -> new RenderParm();
            case "samplershadow2d" -> new RenderParm();
            case "scalar" -> new RenderParm();
            case "si32" -> new RenderParm();
            case "storagetexelbuffer" -> new RenderParm();
            case "string" -> new RenderParm();
            case "struct" -> new RenderParm();
            case "structuredbuffer" -> new RenderParm();
            case "tex" -> new RenderParm();
            case "tex2d" -> new RenderParm();
            case "tex3d" -> new RenderParm();
            case "texarray2d" -> new RenderParm();
            case "texarraycube" -> new RenderParm();
            case "texcube" -> new RenderParm();
            case "texmultisample2d" -> new RenderParm();
            case "texstencil" -> new RenderParm();
            case "ui32" -> new RenderParm();
            case "uniformbuffer" -> new RenderParm();
            case "uniformtexelbuffer" -> new RenderParm();
            case "vec" -> new RenderParm();
            default -> throw new DeclParseException("Unknown renderparm type: " + name);
        };
    }
}
