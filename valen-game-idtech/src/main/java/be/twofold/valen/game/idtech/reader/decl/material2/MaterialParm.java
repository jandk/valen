package be.twofold.valen.game.idtech.reader.decl.material2;

import be.twofold.valen.core.util.*;
import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import com.google.gson.*;
import wtf.reversed.toolbox.math.*;

public record MaterialParm(
    String name,
    RenderParm renderParm,
    Object value
) {
    public static MaterialParm parse(String name, JsonElement element, RenderParm renderParm) {
        var value = switch (renderParm.parmType()) {
            case PT_BOOL -> {
                if (element.isJsonObject()) {
                    // TODO: Should we look at X or W?
                    var object = element.getAsJsonObject();
                    if (!object.has("w")) {
                        yield renderParm.declaredValue();
                    }
                    yield object.getAsJsonPrimitive("w").getAsInt() != 0;
                }
                yield element.getAsString();
            }
            case PT_F32, PT_F16 -> {
                if (element.isJsonObject()) {
                    var object = element.getAsJsonObject();
                    if (!object.has("x")) {
                        yield renderParm.declaredValue();
                    }
                    yield object.get("x").getAsFloat();
                }
                yield element.getAsFloat();
            }
            case PT_F32_VEC2, PT_F16_VEC2 -> parseVector2(element, renderParm);
            case PT_F32_VEC3, PT_F16_VEC3 -> parseVector3(element, renderParm);
            case PT_F32_VEC4, PT_F16_VEC4 -> parseVector4(element, renderParm);
            case PT_COLOR_LUT, PT_STRING -> element.getAsString();
            case PT_TEXTURE_2D, PT_TEXTURE_2D_HALF, PT_TEXTURE_3D, PT_TEXTURE_CUBE ->
                MaterialTex.parse(element, renderParm);
            case PT_SI32, PT_UI32 -> element.getAsInt();
            default -> throw new UnsupportedOperationException(renderParm.parmType().toString());
        };

        return new MaterialParm(name, renderParm, value);
    }

    private static Vector2 parseVector2(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var defaultValue = (Vector2) renderParm.declaredValue();
        var x = object.has("x") ? object.getAsJsonPrimitive("x").getAsFloat() : defaultValue.x();
        var y = object.has("y") ? object.getAsJsonPrimitive("y").getAsFloat() : defaultValue.y();
        var result = new Vector2(x, y);
        return renderParm.parmEdit() instanceof ParmEdit.Srgba
            ? new Vector2(Srgb.srgbToLinear(result.x()), Srgb.srgbToLinear(result.y()))
            : result;
    }

    private static Vector3 parseVector3(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var defaultValue = (Vector3) renderParm.declaredValue();
        var x = object.has("x") ? object.getAsJsonPrimitive("x").getAsFloat() : defaultValue.x();
        var y = object.has("y") ? object.getAsJsonPrimitive("y").getAsFloat() : defaultValue.y();
        var z = object.has("z") ? object.getAsJsonPrimitive("z").getAsFloat() : defaultValue.z();
        var result = new Vector3(x, y, z);
        return renderParm.parmEdit() instanceof ParmEdit.Srgba
            ? new Vector3(Srgb.srgbToLinear(result.x()), Srgb.srgbToLinear(result.y()), Srgb.srgbToLinear(result.z()))
            : result;
    }

    private static Vector4 parseVector4(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var defaultValue = (Vector4) renderParm.declaredValue();
        var x = object.has("x") ? object.getAsJsonPrimitive("x").getAsFloat() : defaultValue.x();
        var y = object.has("y") ? object.getAsJsonPrimitive("y").getAsFloat() : defaultValue.y();
        var z = object.has("z") ? object.getAsJsonPrimitive("z").getAsFloat() : defaultValue.z();
        var w = object.has("w") ? object.getAsJsonPrimitive("w").getAsFloat() : defaultValue.w();
        var result = new Vector4(x, y, z, w);
        return renderParm.parmEdit() instanceof ParmEdit.Srgba
            ? new Vector4(Srgb.srgbToLinear(result.x()), Srgb.srgbToLinear(result.y()), Srgb.srgbToLinear(result.z()), result.w())
            : result;
    }
}
