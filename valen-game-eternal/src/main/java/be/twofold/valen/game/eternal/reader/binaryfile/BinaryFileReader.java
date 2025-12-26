package be.twofold.valen.game.eternal.reader.binaryfile;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.*;

public final class BinaryFileReader implements AssetReader<Bytes, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.BinaryFile;
    }

    @Override
    public Bytes read(BinarySource source, EternalAsset asset) throws IOException {
        try {
            var salt = source.readBytes(12);
            var iVec = source.readBytes(16);
            var text = source.readBytes(Math.toIntExact(source.size() - (12 + 16 + 32)));
            var hmac = source.readBytes(32);

            var digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.asBuffer());
            digest.update("swapTeam\n\0".getBytes());
            digest.update(asset.id().fullName().getBytes());
            var key = digest.digest();

            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            mac.update(salt.asBuffer());
            mac.update(iVec.asBuffer());
            mac.update(text.asBuffer());
            var actualHmac = Bytes.wrap(mac.doFinal());

            if (!hmac.equals(actualHmac)) {
                throw new IOException("HMAC mismatch");
            }

            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            var keySpec = new SecretKeySpec(Arrays.copyOfRange(key, 0, 16), "AES");
            var parameterSpec = new IvParameterSpec(iVec.toArray());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

            var result = Bytes.Mutable.allocate(cipher.getOutputSize(text.length()));
            cipher.doFinal(text.asBuffer(), result.asMutableBuffer());
            return result;
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }
}
