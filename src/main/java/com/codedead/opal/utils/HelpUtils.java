package com.codedead.opal.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class HelpUtils {

    private final Logger logger;

    /**
     * Initialize a new HelpUtils
     */
    public HelpUtils() {
        logger = LogManager.getLogger(HelpUtils.class);
        logger.info("Initializing a new HelpUtils object");
    }

    /**
     * Open a file from the local filesystem
     *
     * @param runnableFileOpener The {@link RunnableFileOpener} object that can be used to open the file
     * @throws FileNotFoundException When the file could not be found
     */
    public void openFile(final RunnableFileOpener runnableFileOpener) throws FileNotFoundException {
        if (runnableFileOpener == null)
            throw new NullPointerException("RunnableFileOpener cannot be null!");

        logger.info("Attempting to open file {}", runnableFileOpener.getFileLocation());

        final Path filePath = Paths.get(runnableFileOpener.getFileLocation());

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException(String.format("File (%s) does not exist!", runnableFileOpener.getFileLocation()));
        }

        new Thread(runnableFileOpener).start();
    }

    /**
     * Open a file from resources
     *
     * @param runnableFileOpener The {@link RunnableFileOpener} object that can be used to open the file
     * @param resource           The resource file where the file can be retrieved if it cannot be found on the disk
     * @throws IOException When the file could not be opened
     */
    public void openFileFromResources(final RunnableFileOpener runnableFileOpener, final String resource) throws IOException {
        if (runnableFileOpener == null)
            throw new NullPointerException("RunnableFileOpener cannot be null!");

        logger.info("Attempting to open file from resources {}", resource);

        final Path filePath = Paths.get(runnableFileOpener.getFileLocation());

        // Overwrite the file if it already exists
        if (!Files.exists(filePath)) {
            logger.info("File does not exist on local drive");
            try (final InputStream inputStream = this.getClass().getResourceAsStream(resource)) {
                if (inputStream == null)
                    throw new IOException(String.format("Resource file not found: %s", resource));

                try (final FileOutputStream fos = new FileOutputStream(runnableFileOpener.getFileLocation())) {
                    fos.write(inputStream.readAllBytes());
                }
            }
        }

        new Thread(runnableFileOpener).start();
    }
}
