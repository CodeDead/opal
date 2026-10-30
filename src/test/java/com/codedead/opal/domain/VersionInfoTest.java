package com.codedead.opal.domain;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionInfoTest {

    private static final String VERSION_JSON = """
            {
              "id": "d71f84a7-2994-4d05-8836-f744f7a4df4c",
              "createdAt": "2025-11-15T19:28:42.000000000+00:00",
              "updatedAt": "2025-11-15T19:28:42.000000000+00:00",
              "name": "Opal",
              "description": "Opal is a free and open-source music player that helps you relax and focus on your work.",
              "semver": "v1.5.1",
              "platforms": [
                {
                  "name": "windows",
                  "arch": "x86_64",
                  "portable": false,
                  "downloadUrl": "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-1.5.1.msi",
                  "infoUrl": "https://codedead.com/software/opal/"
                },
                {
                  "name": "windows",
                  "arch": "x86_64",
                  "portable": true,
                  "downloadUrl": "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-win-portable.zip",
                  "infoUrl": "https://codedead.com/software/opal/"
                },
                {
                  "name": "linux",
                  "arch": "x86_64",
                  "portable": true,
                  "downloadUrl": "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-x86_64-1.5.1.AppImage",
                  "infoUrl": "https://codedead.com/software/opal/"
                },
                {
                  "name": "linux",
                  "arch": "x86_64",
                  "portable": false,
                  "downloadUrl": "https://github.com/CodeDead/opal/releases/download/v1.5.1/opal-codedead-1.5.1-1.x86_64.rpm",
                  "infoUrl": "https://codedead.com/software/opal/"
                },
                {
                  "name": "macos",
                  "arch": "aarch64",
                  "portable": false,
                  "downloadUrl": "https://github.com/CodeDead/opal/releases/download/v1.5.1/Opal-1.5.1.dmg",
                  "infoUrl": "https://codedead.com/software/opal/"
                }
              ]
            }
            """;

    @Test
    void mapsVersionResponse() {
        final JsonMapper jsonMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final VersionInfo versionInfo = jsonMapper.readValue(VERSION_JSON, VersionInfo.class);

        assertEquals("d71f84a7-2994-4d05-8836-f744f7a4df4c", versionInfo.getId());
        assertEquals("Opal", versionInfo.getName());
        assertEquals("v1.5.1", versionInfo.getSemver());
        assertEquals(5, versionInfo.getPlatforms().size());

        final PlatformRelease windowsInstaller = versionInfo.getPlatforms().get(0);
        assertEquals("windows", windowsInstaller.getName());
        assertEquals("x86_64", windowsInstaller.getArch());
        assertFalse(windowsInstaller.getPortable());
        assertTrue(windowsInstaller.getDownloadUrl().endsWith(".msi"));
        assertEquals("https://codedead.com/software/opal/", windowsInstaller.getInfoUrl());
    }
}
