package be.twofold.valen.ui.component;

import dagger.*;
import jakarta.inject.*;

import java.util.*;

@Singleton
@Component(modules = {
    ControllerModule.class,
})
interface ControllerFactory {

    Map<Class<?>, Provider<Controller>> controllers();

}
