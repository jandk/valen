package be.twofold.valen.gltf.model;

import org.immutables.value.*;

import java.lang.annotation.*;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Value.Style(
    get = {"is*", "get*"},
    jdk9Collections = true,
    typeAbstract = {"Abstract*", "*Def"},
    typeImmutable = "*Schema",
    visibility = Value.Style.ImplementationVisibility.PUBLIC
)
public @interface SchemaStyle {
}
