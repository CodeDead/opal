package com.codedead.opal.controller;

import com.codedead.opal.domain.PlatformRelease;
import com.codedead.opal.domain.PlatformUpdate;
import com.codedead.opal.domain.VersionInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateControllerTest {

    private static final String UPDATE_URL = "https://example.com/updates.json";
    private static final String CURRENT_VERSION = "1.0.0.0";

    @Test
    void selectsWindowsInstaller() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "WINDOWS", "X86_64", false, "1.5.0.0");

        assertTrue(update.isPresent());
        assertEquals("v1.5.1", update.get().getSemver());
        assertEquals("windows", update.get().getPlatformName());
        assertEquals("x86_64", update.get().getArch());
        assertFalse(update.get().isPortable());
        assertEquals("https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-1.5.1.msi", update.get().getDownloadUrl());
    }

    @Test
    void selectsWindowsPortable() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "windows", "x86_64", true, "1.5.0.0");

        assertTrue(update.isPresent());
        assertTrue(update.get().isPortable());
        assertEquals("https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-win-portable.zip", update.get().getDownloadUrl());
    }

    @Test
    void selectsLinuxAppImage() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "linux", "x86_64", true, "1.5.0.0");

        assertTrue(update.isPresent());
        assertTrue(update.get().isPortable());
        assertTrue(update.get().getDownloadUrl().endsWith(".AppImage"));
    }

    @Test
    void selectsLinuxRpm() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "linux", "x86_64", false, "1.5.0.0");

        assertTrue(update.isPresent());
        assertFalse(update.get().isPortable());
        assertTrue(update.get().getDownloadUrl().endsWith(".rpm"));
    }

    @Test
    void selectsMacosDmg() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "macos", "aarch64", false, "1.5.0.0");

        assertTrue(update.isPresent());
        assertEquals("aarch64", update.get().getArch());
        assertTrue(update.get().getDownloadUrl().endsWith(".dmg"));
    }

    @Test
    void fallsBackToReleaseWithoutPortableFlag() {
        final VersionInfo versionInfo = versionInfo();
        versionInfo.getPlatforms().get(0).setPortable(null);
        versionInfo.getPlatforms().remove(1);

        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo, "windows", "x86_64", true, "1.5.0.0");

        assertTrue(update.isPresent());
        assertTrue(update.get().getDownloadUrl().endsWith(".msi"));
    }

    @Test
    void returnsEmptyForUnknownArchitecture() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "linux", "riscv64", false, "1.5.0.0");

        assertTrue(update.isEmpty());
    }

    @Test
    void returnsEmptyWhenInstalledVersionIsCurrent() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "windows", "x86_64", false, "1.5.1.0");

        assertTrue(update.isEmpty());
    }

    @Test
    void returnsEmptyWhenInstalledVersionIsNewer() {
        final Optional<PlatformUpdate> update = UpdateController.resolveUpdate(versionInfo(), "windows", "x86_64", false, "1.6.0.0");

        assertTrue(update.isEmpty());
    }

    @Test
    void rejectsVersionWithoutSemver() {
        final VersionInfo versionInfo = versionInfo();
        versionInfo.setSemver(" ");

        assertThrows(IllegalArgumentException.class, () -> UpdateController.resolveUpdate(versionInfo, "windows", "x86_64", false, "1.5.0.0"));
    }

    @Test
    void rejectsNullProxyHostWhenEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(NullPointerException.class, () -> updateController.setProxy(true, null, 8080));
    }

    @Test
    void rejectsBlankProxyHostWhenEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(IllegalArgumentException.class, () -> updateController.setProxy(true, " ", 8080));
    }

    @Test
    void rejectsInvalidProxyPort() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(IllegalArgumentException.class, () -> updateController.setProxy(true, "proxy.example.com", 0));
        assertThrows(IllegalArgumentException.class, () -> updateController.setProxy(true, "proxy.example.com", 65536));
    }

    @Test
    void allowsDisabledProxyWithoutConfiguration() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxy(false, null, 0));
    }

    @Test
    void acceptsValidProxyConfiguration() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxy(true, "proxy.example.com", 8080));
    }

    @Test
    void rejectsNullOrBlankUsernameWhenAuthenticationIsEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(NullPointerException.class, () -> updateController.setProxyAuthentication(true, null, "password"));
        assertThrows(IllegalArgumentException.class, () -> updateController.setProxyAuthentication(true, " ", "password"));
    }

    @Test
    void rejectsNullOrEmptyPasswordWhenAuthenticationIsEnabled() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertThrows(NullPointerException.class, () -> updateController.setProxyAuthentication(true, "username", null));
        assertThrows(IllegalArgumentException.class, () -> updateController.setProxyAuthentication(true, "username", ""));
    }

    @Test
    void allowsDisabledAuthenticationWithoutCredentials() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxyAuthentication(false, null, null));
    }

    @Test
    void acceptsValidProxyAuthentication() {
        final UpdateController updateController = new UpdateController(UPDATE_URL, CURRENT_VERSION);

        assertDoesNotThrow(() -> updateController.setProxyAuthentication(true, "username", "password"));
    }

    private static VersionInfo versionInfo() {
        final VersionInfo versionInfo = new VersionInfo();
        versionInfo.setSemver("v1.5.1");
        versionInfo.setPlatforms(new ArrayList<>(List.of(
                release("windows", "x86_64", false, "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-1.5.1.msi"),
                release("windows", "x86_64", true, "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-win-portable.zip"),
                release("linux", "x86_64", true, "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-x86_64-1.5.1.AppImage"),
                release("linux", "x86_64", false, "https://github.com/CodeDead/opal/releases/download/v1.5.1/opal-codedead-1.5.1-1.x86_64.rpm"),
                release("macos", "aarch64", false, "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-1.5.1.dmg")
        )));
        return versionInfo;
    }

    private static PlatformRelease release(final String name, final String arch, final boolean portable, final String downloadUrl) {
        final PlatformRelease release = new PlatformRelease();
        release.setName(name);
        release.setArch(arch);
        release.setPortable(portable);
        release.setDownloadUrl(downloadUrl);
        release.setInfoUrl("https://codedead.com/software/opal/");
        return release;
    }
}
