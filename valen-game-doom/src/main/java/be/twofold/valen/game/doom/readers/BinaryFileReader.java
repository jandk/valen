package be.twofold.valen.game.doom.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.*;

public final class BinaryFileReader implements AssetReader.Binary<ByteBuffer, DoomAsset> {
    @Override
    public boolean canRead(DoomAsset resource) {
        return false;
        // return resource.resourceType().equals("binaryFile");
    }

    @Override
    public ByteBuffer read(BinarySource source, DoomAsset resource, LoadingContext context) throws IOException {
        try {
            var salt = source.readBytes(12);
            var iVec = source.readBytes(16);
            var text = source.readBytes(Math.toIntExact(source.size() - (12 + 16 + 32)));
            var hmac = source.readBytes(32);

            var digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.asBuffer());
            digest.update("swapTeam\n\0".getBytes());
            digest.update(resource.id().fullName().getBytes());
            var key = digest.digest();

            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            mac.update(salt.asBuffer());
            mac.update(iVec.asBuffer());
            mac.update(text.asBuffer());
            var actualHmac = mac.doFinal();

            if (!hmac.equals(Bytes.wrap(actualHmac))) {
                throw new IOException("HMAC mismatch");
            }

            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            var keySpec = new SecretKeySpec(Arrays.copyOfRange(key, 0, 16), "AES");
            var parameterSpec = new IvParameterSpec(iVec.toArray());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            return ByteBuffer.wrap(cipher.doFinal(text.toArray()));
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }
}
