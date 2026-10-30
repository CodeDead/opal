package com.codedead.opal;

import com.codedead.opal.utils.SharedVariables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpalApplicationTest {

    @Test
    void migratesLegacyUpdateApiUrl() {
        assertEquals(SharedVariables.UPDATE_API_URL, OpalApplication.resolveUpdateApi(SharedVariables.LEGACY_UPDATE_API_URL));
    }

    @Test
    void usesDefaultWhenUpdateApiIsMissingOrBlank() {
        assertEquals(SharedVariables.UPDATE_API_URL, OpalApplication.resolveUpdateApi(null));
        assertEquals(SharedVariables.UPDATE_API_URL, OpalApplication.resolveUpdateApi(" "));
    }

    @Test
    void preservesCustomUpdateApiUrl() {
        assertEquals("https://example.com/updates.json", OpalApplication.resolveUpdateApi(" https://example.com/updates.json "));
    }
}
