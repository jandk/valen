package be.twofold.valen.game.eternal.reader.binaryfile;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.*;

public final class BinaryFileReader implements AssetReader<ByteBuffer, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.BinaryFile;
    }

    @Override
    public ByteBuffer read(BinaryReader reader, EternalAsset resource) throws IOException {
        try {
            var salt = reader.readBytes(12);
            var iVec = reader.readBytes(16);
            var text = reader.readBytes(Math.toIntExact(reader.size() - (12 + 16 + 32)));
            var hmac = reader.readBytes(32);

            var digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            digest.update("swapTeam\n\0".getBytes());
            digest.update(resource.id().fullName().getBytes());
            var key = digest.digest();

            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            mac.update(salt);
            mac.update(iVec);
            mac.update(text);
            var actualHmac = mac.doFinal();

            if (!Arrays.equals(hmac, actualHmac)) {
                throw new IOException("HMAC mismatch");
            }

            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            var keySpec = new SecretKeySpec(Arrays.copyOfRange(key, 0, 16), "AES");
            var parameterSpec = new IvParameterSpec(iVec);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            return ByteBuffer.wrap(cipher.doFinal(text));
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }
}
