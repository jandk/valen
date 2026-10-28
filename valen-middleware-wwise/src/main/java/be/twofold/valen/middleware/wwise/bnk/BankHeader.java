package be.twofold.valen.middleware.wwise.bnk;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record BankHeader(
    int bankGeneratorVersion,
    BankId soundBankId,
    int languageId,
    Optional<Boolean> bFeedbackInBank,
    Optional<Short> alignment,
    Optional<Short> deviceAllocated,
    int projectId
) {
    private static final int BYTES = 20;

    public static BankHeader read(BinarySource source, int size) throws IOException {
        var bankGeneratorVersion = source.readInt();
        var soundBankId = BankId.of(source.readInt());
        var languageId = source.readInt();

        Optional<Boolean> feedbackInBankOptional = Optional.empty();
        Optional<Short> alignment = Optional.empty();
        Optional<Short> deviceAllocated = Optional.empty();
        switch (bankGeneratorVersion) {
            case 113:
                feedbackInBankOptional = Optional.of(source.readBool(BoolFormat.INT));
                break;
            case 135:
                alignment = Optional.of(source.readShort());
                deviceAllocated = Optional.of(source.readShort());
                break;
            default:
                throw new IOException("Unsupported bank generator version: " + bankGeneratorVersion);
        }

        var projectId = source.readInt();
        source.skip(size - BYTES);

        return new BankHeader(
            bankGeneratorVersion,
            soundBankId,
            languageId,
            feedbackInBankOptional,
            alignment,
            deviceAllocated,
            projectId
        );
    }
}
