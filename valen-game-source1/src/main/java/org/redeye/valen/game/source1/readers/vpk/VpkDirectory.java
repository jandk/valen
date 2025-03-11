package org.redeye.valen.game.source1.readers.vpk;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
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
        Check.notNull(header, "header");
        entries = List.copyOf(entries);
    }

    public static VpkDirectory read(DataSource source) throws IOException {
        source.expectInt(0x55AA1234);
        var version = VpkVersion.fromValue(source.readInt());
        source.position(0);

        var headerBuffer = source.readBuffer(version.size());
        var header = VpkHeader.read(DataSource.fromBuffer(headerBuffer));

        var treeBuffer = source.readBuffer(header.treeSize());
        var entries = readTree(DataSource.fromBuffer(treeBuffer));
        if (header.version() == VpkVersion.ONE) {
            return new VpkDirectory(header, entries, List.of(), Optional.empty(), Optional.empty());
        }

        if ((header.archiveMD5Size() % 28) != 0) {
            throw new IOException("archive MD5 section size is not a multiple of 28");
        }
        var archiveMD5Buffer = source.readBuffer(header.archiveMD5Size());
        var archiveMD5Entries = DataSource.fromBuffer(archiveMD5Buffer)
            .readObjects(header.archiveMD5Size() / 28, VpkArchiveMD5Entry::read);

        var otherMD5Buffer = source.readBuffer(header.otherMD5Size());
        var otherMD5 = VpkOtherMD5.read(DataSource.fromBuffer(otherMD5Buffer));
        if (!verifyMD5(otherMD5, headerBuffer, treeBuffer, archiveMD5Buffer)) {
            System.err.println("Failed to verify MD5 signatures");
        }

        var signature = VpkSignature.read(source);
        if (!verifySignature(signature, headerBuffer, treeBuffer, archiveMD5Buffer, otherMD5Buffer)) {
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

    private static List<VpkEntry> readTree(DataSource source) throws IOException {
        var entries = new ArrayList<VpkEntry>();
        for (String extension; !(extension = source.readCString()).isEmpty(); ) {
            for (String directory; !(directory = source.readCString()).isEmpty(); ) {
                for (String filename; !(filename = source.readCString()).isEmpty(); ) {
                    entries.add(VpkEntry.read(source, extension, directory, filename));
                }
            }
        }
        return List.copyOf(entries);
    }

    private static boolean verifyMD5(
        VpkOtherMD5 otherMD5,
        ByteBuffer headerBuffer,
        ByteBuffer treeBuffer,
        ByteBuffer archiveMD5Buffer
    ) {
        MD5.reset();
        MD5.update(treeBuffer.rewind());
        if (!Arrays.equals(MD5.digest(), otherMD5.treeChecksum())) {
            return false;
        }

        MD5.reset();
        MD5.update(archiveMD5Buffer.rewind());
        if (!Arrays.equals(MD5.digest(), otherMD5.archiveMD5Checksum())) {
            return false;
        }

        MD5.reset();
        MD5.update(headerBuffer.rewind());
        MD5.update(treeBuffer.rewind());
        MD5.update(archiveMD5Buffer.rewind());
        MD5.update(otherMD5.treeChecksum());
        MD5.update(otherMD5.archiveMD5Checksum());
        if (!Arrays.equals(MD5.digest(), otherMD5.wholeFileChecksum())) {
            return false;
        }

        return true;
    }

    private static boolean verifySignature(
        VpkSignature vpkSignature,
        ByteBuffer headerBuffer,
        ByteBuffer treeBuffer,
        ByteBuffer archiveMD5Buffer,
        ByteBuffer otherMD5Buffer
    ) {
        try {
            var keySpec = new X509EncodedKeySpec(vpkSignature.publicKey());
            var publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

            var signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(headerBuffer.rewind());
            signature.update(treeBuffer.rewind());
            signature.update(archiveMD5Buffer.rewind());
            signature.update(otherMD5Buffer.rewind());
            return signature.verify(vpkSignature.signature());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
