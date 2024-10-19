package org.redeye.valen.game.spacemarines2.types.template;

import org.redeye.valen.game.spacemarines2.fio.*;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.RECORD_COMPONENT)
public @interface FioField {
    Class<? extends FioSerializer<?>> serializer();

    int flags() default 0;
}
