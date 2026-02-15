package be.twofold.valen.core.util;

import java.util.*;

public record Platform(OS os, Arch arch) {
    private static final Platform CURRENT = new Platform(
        OS.current(),
        Arch.current()
    );

    public static Platform current() {
        return CURRENT;
    }

    @Override
    public String toString() {
        return os.name().toLowerCase() + "-" + arch.name().toLowerCase();
    }

    public enum Arch {
        X86_64,
        X86,
        ARM_64,
        ARM,
        ;

        private static final Arch CURRENT;

        static {
            String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
            CURRENT = switch (arch) {
                case "x86_64", "amd64", "em64t", "universal" -> X86_64;
                case "x86", "i386", "i486", "i586", "i686", "pentium" -> X86;
                case "aarch64" -> ARM_64;
                case "arm", "armv7l", "armv8l" -> ARM;
                default -> throw new ExceptionInInitializerError("Unsupported Architecture: " + arch);
            };
        }

        public static Arch current() {
            return CURRENT;
        }
    }

    public enum OS {
        LINUX,
        WINDOWS,
        MAC,
        ;

        private static final OS CURRENT;

        static {
            String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            CURRENT = switch (os.split(" ")[0]) {
                case "linux" -> LINUX;
                case "windows" -> WINDOWS;
                case "mac" -> MAC;
                default -> throw new ExceptionInInitializerError("Unsupported OS: " + os);
            };
        }

        public static OS current() {
            return CURRENT;
        }
    }
}
