package com.codedead.opal.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.security.GeneralSecurityException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PasswordCipherTest {

    @TempDir
    private Path tempDirectory;

    @Test
    void encryptsAndDecryptsPassword() throws IOException, GeneralSecurityException {
        final PasswordCipher passwordCipher = new PasswordCipher(tempDirectory.resolve("proxy.key"));

        final String encrypted = passwordCipher.encrypt("secret");

        assertTrue(encrypted.startsWith("enc:v1:"));
        assertNotEquals("secret", encrypted);
        assertEquals("secret", passwordCipher.decrypt(encrypted));
    }

    @Test
    void usesRandomIvForEachEncryption() throws IOException, GeneralSecurityException {
        final PasswordCipher passwordCipher = new PasswordCipher(tempDirectory.resolve("proxy.key"));

        assertNotEquals(passwordCipher.encrypt("secret"), passwordCipher.encrypt("secret"));
    }

    @Test
    void returnsLegacyPlaintextValues() throws IOException, GeneralSecurityException {
        final PasswordCipher passwordCipher = new PasswordCipher(tempDirectory.resolve("proxy.key"));

        assertEquals("legacy", passwordCipher.decrypt("legacy"));
    }

    @Test
    void encryptsAndDecryptsEmptyValues() throws IOException, GeneralSecurityException {
        final PasswordCipher passwordCipher = new PasswordCipher(tempDirectory.resolve("proxy.key"));

        assertEquals("", passwordCipher.encrypt(""));
        assertEquals("", passwordCipher.decrypt(""));
    }

    @Test
    void rejectsTamperedCiphertext() throws IOException, GeneralSecurityException {
        final PasswordCipher passwordCipher = new PasswordCipher(tempDirectory.resolve("proxy.key"));
        final String encrypted = passwordCipher.encrypt("secret");

        final byte[] payload = Base64.getDecoder().decode(encrypted.substring("enc:v1:".length()));
        payload[payload.length - 1] ^= 1;

        final String tampered = "enc:v1:" + Base64.getEncoder().encodeToString(payload);

        assertThrows(GeneralSecurityException.class, () -> passwordCipher.decrypt(tampered));
    }

    @Test
    void rejectsMalformedCiphertext() {
        final PasswordCipher passwordCipher = new PasswordCipher(tempDirectory.resolve("proxy.key"));

        assertThrows(GeneralSecurityException.class, () -> passwordCipher.decrypt("enc:v1:not-base64!"));
    }

    @Test
    void reusesTheStoredKey() throws IOException, GeneralSecurityException {
        final Path keyFile = tempDirectory.resolve("proxy.key");
        final PasswordCipher firstCipher = new PasswordCipher(keyFile);
        final String encrypted = firstCipher.encrypt("secret");

        final PasswordCipher secondCipher = new PasswordCipher(keyFile);

        assertTrue(Files.exists(keyFile));
        assertEquals("secret", secondCipher.decrypt(encrypted));
    }

    @Test
    void failsToDecryptWhenTheKeyFileIsMissing() throws IOException, GeneralSecurityException {
        final Path keyFile = tempDirectory.resolve("proxy.key");
        final String encrypted = new PasswordCipher(keyFile).encrypt("secret");
        Files.delete(keyFile);

        final PasswordCipher passwordCipher = new PasswordCipher(keyFile);

        assertThrows(IOException.class, () -> passwordCipher.decrypt(encrypted));
        assertTrue(Files.notExists(keyFile));
    }

    @Test
    void storesTheKeyWithOwnerOnlyPermissions() throws IOException, GeneralSecurityException {
        final Path keyFile = tempDirectory.resolve("proxy.key");
        new PasswordCipher(keyFile).encrypt("secret");

        assumeTrue(Files.getFileStore(keyFile).supportsFileAttributeView(PosixFileAttributeView.class));

        final String permissions = Files.getPosixFilePermissions(keyFile).toString();

        assertEquals("[OWNER_READ, OWNER_WRITE]", permissions);
    }
}
