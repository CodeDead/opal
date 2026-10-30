package com.codedead.opal.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemverTest {

    @Test
    void treatsLeadingVAndTrailingZeroAsEqual() {
        assertEquals(0, Semver.compare("v1.5.1", "1.5.1.0"));
        assertEquals(0, Semver.compare("V1.5.1", "1.5.1"));
    }

    @Test
    void comparesNumericComponents() {
        assertTrue(Semver.compare("1.10.0", "1.9.0") > 0);
        assertTrue(Semver.compare("2.0.0", "1.9.9") > 0);
        assertTrue(Semver.compare("1.5.0", "1.5.1") < 0);
    }

    @Test
    void comparesPrereleaseVersions() {
        assertTrue(Semver.compare("1.5.1", "1.5.1-rc.1") > 0);
        assertTrue(Semver.compare("1.5.1-rc.2", "1.5.1-rc.1") > 0);
    }

    @Test
    void ignoresBuildMetadata() {
        assertEquals(0, Semver.compare("1.5.1+build.2", "1.5.1"));
    }

    @Test
    void rejectsNullOrBlankVersions() {
        assertThrows(NullPointerException.class, () -> Semver.compare(null, "1.0.0"));
        assertThrows(IllegalArgumentException.class, () -> Semver.compare(" ", "1.0.0"));
    }
}
