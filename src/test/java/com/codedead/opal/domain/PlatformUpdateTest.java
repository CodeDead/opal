package com.codedead.opal.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformUpdateTest {

    @Test
    void comparesEachVersionComponent() {
        assertOlder(newUpdate(1, 9, 9, 9), newUpdate(2, 0, 0, 0));
        assertOlder(newUpdate(1, 2, 9, 9), newUpdate(1, 3, 0, 0));
        assertOlder(newUpdate(1, 2, 3, 9), newUpdate(1, 2, 4, 0));
        assertOlder(newUpdate(1, 2, 3, 4), newUpdate(1, 2, 3, 5));
    }

    @Test
    void returnsZeroForEqualVersions() {
        assertEquals(0, newUpdate(1, 2, 3, 4).compareTo(newUpdate(1, 2, 3, 4)));
    }

    @Test
    void comparisonIsSymmetric() {
        final PlatformUpdate first = newUpdate(1, 2, 3, 4);
        final PlatformUpdate second = newUpdate(1, 2, 3, 5);

        assertEquals(-first.compareTo(second), second.compareTo(first));
    }

    @Test
    void rejectsNull() {
        assertThrows(NullPointerException.class, () -> newUpdate(1, 2, 3, 4).compareTo(null));
    }

    private static void assertOlder(final PlatformUpdate older, final PlatformUpdate newer) {
        assertEquals(-1, older.compareTo(newer));
        assertEquals(1, newer.compareTo(older));
    }

    private static PlatformUpdate newUpdate(final int major, final int minor, final int build, final int revision) {
        final PlatformUpdate update = new PlatformUpdate();
        update.setMajorVersion(major);
        update.setMinorVersion(minor);
        update.setBuildVersion(build);
        update.setRevisionVersion(revision);
        return update;
    }
}
