package com.codedead.opal.utils;

import com.codedead.opal.interfaces.IRunnableHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class HelpUtils {

    private static final Logger logger = LogManager.getLogger(HelpUtils.class);

    /**
     * Initialize a new HelpUtils
     */
    public HelpUtils() {
        logger.info("Initializing a new HelpUtils object");
    }

    /**
     * Open the CodeDead website
     *
     * @param url The url that should be opened
     */
    public final void openWebsite(final String url) {
        logger.info(String.format("Attempting to open website %s", url));

        if (Desktop.isDesktopSupported()) {
            final RunnableSiteOpener siteOpener = new RunnableSiteOpener(url, new IRunnableHelper() {
                @Override
                public final void executed() {
                    // ignored
                }

                @Override
                public final void exceptionOccurred(final Exception ex) {
                    logger.error(String.format("Unable to open website %s", url), ex);
                }
            });
            new Thread(siteOpener).start();
        } else {
            logger.info("Desktop is not supported");
        }
    }

    /**
     * Open a file from resources
     *
     * @param location The location where the file can be found
     * @param resource The resource file where the file can be retrieved if it cannot be found on the disk
     * @throws IOException When the file could not be opened
     */
    public final void openFileFromResources(final String location, final String resource) throws IOException {
        logger.info(String.format("Attempting to open file from resources %s", resource));

        if (Desktop.isDesktopSupported()) {
            final Path filePath = Paths.get(location);

            // Check if file already exists
            if (!Files.exists(filePath)) {
                logger.info("File does not exist on local drive");
                // Write the file
                try (final InputStream jarPdf = this.getClass().getResourceAsStream(resource)) {
                    if (jarPdf == null) throw new IOException("Resource file not found!");

                    try (final FileOutputStream fos = new FileOutputStream(location)) {
                        fos.write(jarPdf.readAllBytes());
                    }
                }
            }
            final RunnableFileOpener opener = new RunnableFileOpener(location, new IRunnableHelper() {
                @Override
                public final void executed() {
                    // ignored
                }

                @Override
                public final void exceptionOccurred(final Exception ex) {
                    logger.error(String.format("Unable to open file %s", resource), ex);
                }
            });
            new Thread(opener).start();
        } else {
            logger.info("Desktop is not supported");
        }
    }
}
