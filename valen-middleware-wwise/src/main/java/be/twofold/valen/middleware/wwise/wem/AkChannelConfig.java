package be.twofold.valen.middleware.wwise.wem;

public record AkChannelConfig(
    int channels,
    AkChannelConfigType type,
    int channelMask
) {
    static AkChannelConfig parse(int raw, int channels) {
        int type = (raw >>> 8) & 0x0F;
        if ((raw & 0xFF) != channels || type > 2) {
            return new AkChannelConfig(Integer.bitCount(raw), AkChannelConfigType.STANDARD, raw);
        }

        return new AkChannelConfig(
            raw & 0xFF,
            AkChannelConfigType.fromValue(type),
            raw >>> 12
        );
    }
}
