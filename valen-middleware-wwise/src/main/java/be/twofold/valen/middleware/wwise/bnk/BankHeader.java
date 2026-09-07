package be.twofold.valen.middleware.wwise.bnk;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record BankHeader(
    int bankGeneratorVersion,
    int soundBankId,
    int languageId,
    boolean bFeedbackInBank,
    int projectId
) {
    private static final int BYTES = 20;

    public static BankHeader read(BinarySource source, int size) throws IOException {
        var bankGeneratorVersion = source.readInt();
        var soundBankId = source.readInt();
        var languageId = source.readInt();
        var bFeedbackInBank = source.readBool(BoolFormat.INT);
        var projectId = source.readInt();
        source.skip(size - BYTES);

        return new BankHeader(
            bankGeneratorVersion,
            soundBankId,
            languageId,
            bFeedbackInBank,
            projectId
        );
    }
}
