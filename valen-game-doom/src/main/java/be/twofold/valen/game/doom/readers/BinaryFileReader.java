package be.twofold.valen.game.doom.readers;

import be.twofold.valen.core.game.AssetReader;
import be.twofold.valen.core.io.DataSource;
import be.twofold.valen.game.doom.*;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

public final class BinaryFileReader implements AssetReader<ByteBuffer, DoomAsset> {
    @Override
    public boolean canRead(DoomAsset resource) {
        return resource.resourceType().equals("binaryFile");
    }

    @Override
    public ByteBuffer read(DataSource source, DoomAsset resource) throws IOException {
        try {
            var salt = source.readBytes(12);
            var iVec = source.readBytes(16);
            var text = source.readBytes(Math.toIntExact(source.size() - (12 + 16 + 32)));
            var hmac = source.readBytes(32);

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
