package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;
import wtf.reversed.toolbox.util.*;

import static org.assertj.core.api.Assertions.*;

class PlatformTest {
    @Test
    void testCurrent() {
        System.setProperty("os.name", "Linux");
        assertThat(Platform.OS.current()).isEqualTo(Platform.OS.LINUX);
    }
}
