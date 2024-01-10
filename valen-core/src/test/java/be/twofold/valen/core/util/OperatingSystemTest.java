package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class OperatingSystemTest {
    @Test
    void testCurrent() {
        System.setProperty("os.name", "Linux");
        assertThat(OperatingSystem.current()).isEqualTo(OperatingSystem.Linux);
    }
}
