package dynamic.solidwater.tools.aes;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

record Secrets(SecretKey key, byte[] initializationVector) {

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    static Secrets fromEncoded(final String keyAndIVEncoded) {
        final var secrets = keyAndIVEncoded.split("@");
        return new Secrets(new SecretKeySpec(DECODER.decode(secrets[0]), "AES"), DECODER.decode(secrets[1]));
    }

}
