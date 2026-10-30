package com.codedead.opal.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordCipher {

    private static final String PREFIX = "enc:v1:";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH = 32;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final Path keyFile;
    private final SecureRandom secureRandom;
    private final Logger logger;

    private byte[] key;

    /**
     * Initialize a new PasswordCipher that stores its key in the default location
     */
    public PasswordCipher() {
        this(Path.of(SharedVariables.PROXY_PASSWORD_KEY_FILE_LOCATION));
    }

    /**
     * Initialize a new PasswordCipher
     *
     * @param keyFile The location of the file that contains the encryption key
     */
    public PasswordCipher(final Path keyFile) {
        if (keyFile == null)
            throw new NullPointerException("Key file cannot be null!");

        this.keyFile = keyFile;
        this.secureRandom = new SecureRandom();
        this.logger = LogManager.getLogger(PasswordCipher.class);
    }

    /**
     * Encrypt a value
     *
     * @param value The value that should be encrypted
     * @return The encrypted value, or an empty {@link String} if the value was empty
     * @throws IOException              When the encryption key could not be loaded or stored
     * @throws GeneralSecurityException When the value could not be encrypted
     */
    public String encrypt(final String value) throws IOException, GeneralSecurityException {
        if (value == null)
            throw new NullPointerException("Value cannot be null!");
        if (value.isEmpty())
            return "";

        final byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(true), new GCMParameterSpec(TAG_LENGTH, iv));
        final byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

        final byte[] payload = new byte[iv.length + encryptedValue.length];
        System.arraycopy(iv, 0, payload, 0, iv.length);
        System.arraycopy(encryptedValue, 0, payload, iv.length, encryptedValue.length);

        return PREFIX + Base64.getEncoder().encodeToString(payload);
    }

    /**
     * Decrypt a value
     *
     * @param value The value that should be decrypted
     * @return The decrypted value
     * @throws IOException              When the encryption key could not be loaded
     * @throws GeneralSecurityException When the value could not be decrypted
     */
    public String decrypt(final String value) throws IOException, GeneralSecurityException {
        if (value == null)
            throw new NullPointerException("Value cannot be null!");
        if (!value.startsWith(PREFIX))
            return value;

        final byte[] payload;
        try {
            payload = Base64.getDecoder().decode(value.substring(PREFIX.length()));
        } catch (final IllegalArgumentException ex) {
            throw new GeneralSecurityException("Encrypted value is invalid", ex);
        }

        if (payload.length <= IV_LENGTH)
            throw new GeneralSecurityException("Encrypted value is invalid");

        final byte[] iv = new byte[IV_LENGTH];
        final byte[] encryptedValue = new byte[payload.length - IV_LENGTH];
        System.arraycopy(payload, 0, iv, 0, IV_LENGTH);
        System.arraycopy(payload, IV_LENGTH, encryptedValue, 0, encryptedValue.length);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKey(false), new GCMParameterSpec(TAG_LENGTH, iv));

        return new String(cipher.doFinal(encryptedValue), StandardCharsets.UTF_8);
    }

    /**
     * Retrieve the encryption key, creating it if requested
     *
     * @param createIfMissing True if the key should be created when it does not exist, otherwise false
     * @return The {@link SecretKey} object
     * @throws IOException When the key could not be loaded or created
     */
    private SecretKey getKey(final boolean createIfMissing) throws IOException {
        if (key == null) {
            if (Files.exists(keyFile)) {
                key = readKey();
            } else if (createIfMissing) {
                key = createKey();
            } else {
                throw new IOException(String.format("Proxy password key file does not exist (%s)", keyFile));
            }
        }

        return new SecretKeySpec(key, "AES");
    }

    /**
     * Read the encryption key from the key file
     *
     * @return The encryption key
     * @throws IOException When the key file could not be read or is invalid
     */
    private byte[] readKey() throws IOException {
        final byte[] storedKey = Files.readAllBytes(keyFile);
        if (storedKey.length != KEY_LENGTH)
            throw new IOException(String.format("Invalid proxy password key file (%s)", keyFile));

        return storedKey;
    }

    /**
     * Create a new encryption key and store it in the key file
     *
     * @return The encryption key
     * @throws IOException When the key file could not be created
     */
    private byte[] createKey() throws IOException {
        final Path parent = keyFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        final byte[] generatedKey = new byte[KEY_LENGTH];
        secureRandom.nextBytes(generatedKey);

        try {
            Files.write(keyFile, generatedKey, StandardOpenOption.CREATE_NEW);
        } catch (final FileAlreadyExistsException ex) {
            return readKey();
        }

        try {
            Files.setPosixFilePermissions(keyFile, PosixFilePermissions.fromString("rw-------"));
        } catch (final IOException | UnsupportedOperationException ex) {
            logger.warn("Unable to restrict the permissions of the proxy password key file", ex);
        }

        return generatedKey;
    }
}
