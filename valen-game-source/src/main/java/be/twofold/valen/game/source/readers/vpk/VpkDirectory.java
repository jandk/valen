package be.twofold.valen.game.source.readers.vpk;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public record VpkDirectory(
    VpkHeader header,
    List<VpkEntry> entries,
    List<VpkArchiveMD5Entry> archiveMD5Entries,
    Optional<VpkOtherMD5> otherMD5,
    Optional<VpkSignature> signature
) {
    private static final MessageDigest MD5;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public VpkDirectory {
        Check.nonNull(header, "header");
        entries = List.copyOf(entries);
    }

    public static VpkDirectory read(BinarySource source) throws IOException {
        source.expectInt(VpkHeader.MAGIC);
        var version = VpkVersion.fromValue(source.readInt());
        source.position(0);

        var headerBuffer = source.readBytes(version.size());
        var header = VpkHeader.read(BinarySource.wrap(headerBuffer));

        var treeBuffer = source.readBytes(header.treeSize());
        var entries = readTree(BinarySource.wrap(treeBuffer));
        if (header.version() == VpkVersion.ONE) {
            return new VpkDirectory(header, entries, List.of(), Optional.empty(), Optional.empty());
        }

        if ((header.archiveMD5Size() % 28) != 0) {
            throw new IOException("archive MD5 section size is not a multiple of 28");
        }
        var archiveMD5Buffer = source.readBytes(header.archiveMD5Size());
        var archiveMD5Entries = BinarySource.wrap(archiveMD5Buffer)
            .readObjects(header.archiveMD5Size() / 28, VpkArchiveMD5Entry::read);

        var otherMD5Buffer = source.readBytes(header.otherMD5Size());
        var otherMD5 = VpkOtherMD5.read(BinarySource.wrap(otherMD5Buffer));
        if (!verifyMD5(otherMD5, headerBuffer, treeBuffer, archiveMD5Buffer)) {
            System.err.println("Failed to verify MD5 signatures");
        }

        var signature = VpkSignature.read(source);
        var buffers = header.signatureSize() != 20
            ? List.of(headerBuffer, treeBuffer, archiveMD5Buffer, otherMD5Buffer)
            : List.of(otherMD5.wholeFileChecksum());
        if (!verifySignature(signature, buffers)) {
            System.err.println("Failed to verify signature");
        }

        return new VpkDirectory(
            header,
            entries,
            archiveMD5Entries,
            Optional.of(otherMD5),
            Optional.of(signature)
        );
    }

    private static List<VpkEntry> readTree(BinarySource source) throws IOException {
        var entries = new ArrayList<VpkEntry>();
        for (String extension; !(extension = source.readString(StringFormat.NULL_TERM)).isEmpty(); ) {
            for (String directory; !(directory = source.readString(StringFormat.NULL_TERM)).isEmpty(); ) {
                for (String filename; !(filename = source.readString(StringFormat.NULL_TERM)).isEmpty(); ) {
                    entries.add(VpkEntry.read(source, extension, directory, filename));
                }
            }
        }
        return List.copyOf(entries);
    }

    private static boolean verifyMD5(
        VpkOtherMD5 otherMD5,
        Bytes headerBuffer,
        Bytes treeBuffer,
        Bytes archiveMD5Buffer
    ) {
        MD5.reset();
        MD5.update(treeBuffer.asBuffer());
        if (!otherMD5.treeChecksum().equals(Bytes.wrap(MD5.digest()))) {
            return false;
        }

        MD5.reset();
        MD5.update(archiveMD5Buffer.asBuffer());
        if (!otherMD5.archiveMD5Checksum().equals(Bytes.wrap(MD5.digest()))) {
            return false;
        }

        MD5.reset();
        MD5.update(headerBuffer.asBuffer());
        MD5.update(treeBuffer.asBuffer());
        MD5.update(archiveMD5Buffer.asBuffer());
        MD5.update(otherMD5.treeChecksum().asBuffer());
        MD5.update(otherMD5.archiveMD5Checksum().asBuffer());
        if (!otherMD5.wholeFileChecksum().equals(Bytes.wrap(MD5.digest()))) {
            return false;
        }

        return true;
    }

    private static boolean verifySignature(
        VpkSignature vpkSignature,
        List<Bytes> buffers
    ) {
        try {
            var keySpec = new X509EncodedKeySpec(vpkSignature.publicKey().toArray());
            var publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

            var signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            for (Bytes buffer : buffers) {
                signature.update(buffer.asBuffer());
            }
            return signature.verify(vpkSignature.signature().toArray());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
