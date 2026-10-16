package be.twofold.valen.middleware.wwise.shared;

import wtf.reversed.toolbox.hash.*;

import java.util.*;

public record BankId(
    int value
) {
    private static final HashFunction hash = HashFunction.fnv1_32();

    public static BankId of(int value) {
        return new BankId(value);
    }

    public static BankId of(String name) {
        var lower = name.toLowerCase(Locale.ROOT);
        return new BankId(hash.hash(lower).asInt());
    }

    public static BankId parse(String value) {
        return new BankId(Integer.parseUnsignedInt(value));
    }

    @Override
    public String toString() {
        return Integer.toUnsignedString(value);
    }
}
