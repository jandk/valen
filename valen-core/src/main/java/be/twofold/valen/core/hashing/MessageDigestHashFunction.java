package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

import java.security.*;

public final class MessageDigestHashFunction implements HashFunction {
    private final MessageDigest digest;

    public MessageDigestHashFunction(String algorithm) {
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public HashCode hash(Bytes src) {
        digest.update(src.asBuffer());
        byte[] result = digest.digest();
        return HashCode.ofBytes(Bytes.wrap(result));
    }
}
