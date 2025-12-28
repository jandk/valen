package be.twofold.valen.format.gltf.model;

import org.immutables.value.*;

import java.lang.annotation.*;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(
    defaults = @Value.Immutable(copy = false),
    depluralize = true,
    depluralizeDictionary = "mesh:meshes",
    from = "",
    get = {"is*", "get*"},
    jdk9Collections = true,
    typeAbstract = "*Schema",
    visibility = Value.Style.ImplementationVisibility.PUBLIC
)
public @interface SchemaStyle {
}
