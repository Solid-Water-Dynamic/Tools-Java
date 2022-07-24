package dynamic.solidwater.tools.aes;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.function.Consumer;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class AES {

    private static final String JAR = "AES-1.0.jar"; // TODO How not to hard code this version number?

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    private static final String NEWLINE = System.getProperty("line.separator");

    private static final Consumer<String> LOG = System.out::println;

    public static void main(final String[] args) throws IOException, GeneralSecurityException {
        if (args.length == 2 && "encrypt".equalsIgnoreCase(args[0])) {
            final var inputFile = new File(args[1]);
            final var encrypted = encrypt(readFile(inputFile));

            writeFile(new File(inputFile.getName() + ".ciphertext"), encrypted.ciphertext());

            LOG.accept("Keep the secrets: " + encrypted.encodedSecrets());
        } else if (args.length == 3 && "decrypt".equalsIgnoreCase(args[0])) {
            final var inputFile = new File(args[1]);
            final var plaintext = decrypt(readFile(inputFile), args[2]);

            writeFile(new File(inputFile.getName() + ".plaintext"), plaintext);
        } else {
            LOG.accept("Usage:" + NEWLINE +
                       "java -jar " + JAR + " encrypt originalFile" + NEWLINE +
                       "java -jar " + JAR + " decrypt encryptedFile secrets");
        }
    }

    private static byte[] readFile(final File file) throws IOException {
        LOG.accept("Reading " + file.getCanonicalPath());
        return Files.readAllBytes(file.toPath());
    }

    private static void writeFile(final File file, final byte[] content) throws IOException {
        LOG.accept("Writing " + file.getCanonicalPath());
        Files.write(file.toPath(), content);
    }

    private static Encrypted encrypt(final byte[] plaintext) throws GeneralSecurityException {
        LOG.accept("Encrypting " + plaintext.length + " bytes");

        final var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // AES_KEY_SIZE

        final var key = keyGenerator.generateKey();

        final var initializationVector = new byte[12]; // GCM_IV_LENGTH
        new SecureRandom().nextBytes(initializationVector);

        final var ciphertext = getCipher(ENCRYPT_MODE, new Secrets(key, initializationVector)).doFinal(plaintext);

        final var encodedSecrets = ENCODER.encodeToString(key.getEncoded()) + "@" + ENCODER.encodeToString(initializationVector);

        return new Encrypted(ciphertext, encodedSecrets);
    }

    private static byte[] decrypt(final byte[] ciphertext, final String encodedSecrets) throws GeneralSecurityException {
        LOG.accept("Decrypting " + ciphertext.length + " bytes");

        return getCipher(DECRYPT_MODE, Secrets.fromEncoded(encodedSecrets)).doFinal(ciphertext);
    }

    private static Cipher getCipher(final int mode, final Secrets secrets) throws GeneralSecurityException {
        final var secretKeySpec = new SecretKeySpec(secrets.key().getEncoded(), "AES");

        final var algorithmParameterSpec = new GCMParameterSpec(16 * 8, secrets.initializationVector()); // GCM_TAG_LENGTH

        final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, secretKeySpec, algorithmParameterSpec);

        return cipher;
    }

}