package be.twofold.valen.reader.binaryfile;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.*;

public final class BinaryFileReader implements ResourceReader<byte[]> {
    @Inject
    BinaryFileReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.BinaryFile;
    }

    @Override
    public byte[] read(DataSource source, Resource resource) throws IOException {
        try {
            var salt = source.readBytes(12);
            var iVec = source.readBytes(16);
            var text = source.readBytes(Math.toIntExact(source.size() - (12 + 16 + 32)));
            var hmac = source.readBytes(32);

            var digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            digest.update("swapTeam\n\0".getBytes());
            digest.update(resource.nameString().getBytes());
            var key = digest.digest();

            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            mac.update(salt);
            mac.update(iVec);
            mac.update(text);
            var actualHmac = mac.doFinal();

            if (!Arrays.equals(hmac, actualHmac)) {
                throw new RuntimeException("HMAC mismatch");
            }

            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            var keySpec = new SecretKeySpec(Arrays.copyOfRange(key, 0, 16), "AES");
            var parameterSpec = new IvParameterSpec(iVec);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            return cipher.doFinal(text);
        } catch (Exception e) {
            throw new RuntimeException("Error reading binary file", e);
        }
    }
}
