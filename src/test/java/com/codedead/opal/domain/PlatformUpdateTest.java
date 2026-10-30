package com.codedead.opal.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformUpdateTest {

    @Test
    void comparesEachVersionComponent() {
        assertOlder(newUpdate("v1.9.9"), newUpdate("v2.0.0"));
        assertOlder(newUpdate("v1.2.9"), newUpdate("v1.3.0"));
        assertOlder(newUpdate("v1.2.3.9"), newUpdate("v1.2.4"));
        assertOlder(newUpdate("v1.2.3.4"), newUpdate("v1.2.3.5"));
    }

    @Test
    void returnsZeroForEqualVersions() {
        assertEquals(0, newUpdate("v1.5.1").compareTo(newUpdate("1.5.1.0")));
    }

    @Test
    void comparisonIsSymmetric() {
        final PlatformUpdate first = newUpdate("v1.2.3.4");
        final PlatformUpdate second = newUpdate("v1.2.3.5");

        assertEquals(-first.compareTo(second), second.compareTo(first));
    }

    @Test
    void rejectsNull() {
        assertThrows(NullPointerException.class, () -> newUpdate("v1.2.3.4").compareTo(null));
    }

    private static void assertOlder(final PlatformUpdate older, final PlatformUpdate newer) {
        assertEquals(-1, older.compareTo(newer));
        assertEquals(1, newer.compareTo(older));
    }

    private static PlatformUpdate newUpdate(final String semver) {
        final PlatformUpdate update = new PlatformUpdate();
        update.setSemver(semver);
        return update;
    }
}
