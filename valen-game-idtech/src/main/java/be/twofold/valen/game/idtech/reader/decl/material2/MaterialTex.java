package be.twofold.valen.game.idtech.reader.decl.material2;

import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import com.google.gson.*;

public record MaterialTex(
    String filePath,
    MaterialImageOpts options
) {
    public static MaterialTex parse(JsonElement element, RenderParm renderParm) {
        var object = element.getAsJsonObject();
        var filePath = object.has("filePath") ? object.get("filePath").getAsString() : null;
        var options = MaterialImageOpts.parse(object.getAsJsonObject("options"), MaterialImageOpts.defaults(renderParm));

        return new MaterialTex(filePath, options);
    }
}
