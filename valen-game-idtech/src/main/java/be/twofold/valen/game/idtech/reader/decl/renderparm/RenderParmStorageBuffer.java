package be.twofold.valen.game.idtech.reader.decl.renderparm;

import be.twofold.valen.game.idtech.lexer.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record RenderParmStorageBuffer(
    Set<BufferViewFlag> flags,
    DescriptorSetLayout descriptorSetLayout,
    String name
) {
    public RenderParmStorageBuffer {
        Check.nonNull(name, "name");
        var flagsCopy = EnumSet.noneOf(BufferViewFlag.class);
        flagsCopy.addAll(flags);
        flags = Collections.unmodifiableSet(flagsCopy);
    }

    public static RenderParmStorageBuffer parse(IdLexer lexer) {
        var flags = EnumSet.noneOf(BufferViewFlag.class);
        DescriptorSetLayout descriptorSetLayout = null;
        while (true) {
            var token = lexer.peekToken();
            if (token == null) {
                break;
            }

            var flag = BufferViewFlag.parse(token.value());
            if (flag.isPresent()) {
                flags.add(flag.get());
                lexer.expectName();
                continue;
            }

            var layout = DescriptorSetLayout.parse(token.value());
            if (layout.isPresent()) {
                descriptorSetLayout = layout.get();
                lexer.expectName();
                continue;
            }

            break;
        }
        var name = lexer.expectName().value();
        return new RenderParmStorageBuffer(flags, descriptorSetLayout, name);
    }

    public enum DescriptorSetLayout {
        INSTANCE,
        SURFACE,
        ;

        public static Optional<DescriptorSetLayout> parse(String layout) {
            return switch (layout.toLowerCase(Locale.ROOT)) {
                case "instancedescriptorsetlayout" -> Optional.of(INSTANCE);
                case "surfacedescriptorsetlayout" -> Optional.of(SURFACE);
                default -> Optional.empty();
            };
        }
    }
}
