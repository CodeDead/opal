package com.codedead.opal.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OsCheckTest {

    @Test
    void normalizesX64Architectures() {
        assertEquals("x86_64", OsCheck.normalizeArchitecture("amd64"));
        assertEquals("x86_64", OsCheck.normalizeArchitecture("x86_64"));
        assertEquals("x86_64", OsCheck.normalizeArchitecture("x64"));
    }

    @Test
    void normalizesArm64Architectures() {
        assertEquals("aarch64", OsCheck.normalizeArchitecture("aarch64"));
        assertEquals("aarch64", OsCheck.normalizeArchitecture("arm64"));
    }

    @Test
    void normalizesX86Architectures() {
        assertEquals("x86", OsCheck.normalizeArchitecture("i386"));
        assertEquals("x86", OsCheck.normalizeArchitecture("i686"));
    }

    @Test
    void preservesUnknownArchitecturesInLowercase() {
        assertEquals("riscv64", OsCheck.normalizeArchitecture("riscv64"));
    }

    @Test
    void rejectsNullArchitecture() {
        assertThrows(NullPointerException.class, () -> OsCheck.normalizeArchitecture(null));
    }
}
