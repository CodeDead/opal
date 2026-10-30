package com.codedead.opal.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HelpUtilsTest {

    private static final String RESOURCE = "/documents/help.pdf";

    @TempDir
    private Path temporaryDirectory;

    @Test
    void refreshesAnExistingCachedFile() throws IOException {
        final Path target = temporaryDirectory.resolve("help.pdf");
        Files.writeString(target, "stale help");

        new HelpUtils().refreshResourceFile(target, RESOURCE);

        try (final InputStream inputStream = HelpUtils.class.getResourceAsStream(RESOURCE)) {
            assert inputStream != null;
            assertArrayEquals(inputStream.readAllBytes(), Files.readAllBytes(target));
        }
    }

    @Test
    void leavesTheExistingFileUntouchedWhenTheResourceIsMissing() throws IOException {
        final Path target = temporaryDirectory.resolve("help.pdf");
        Files.writeString(target, "existing help");

        assertThrows(IOException.class,
                () -> new HelpUtils().refreshResourceFile(target, "/documents/missing.pdf"));

        assertEquals("existing help", Files.readString(target));
    }
}
