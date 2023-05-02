package com.codedead.opal.utils;

import com.codedead.opal.interfaces.IRunnableHelper;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

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

        if (!Files.exists(filePath))
            throw new FileNotFoundException(String.format("File (%s) does not exist!", runnableFileOpener.getFileLocation()));

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
        if (resource == null)
            throw new NullPointerException("Resource cannot be null!");
        if (resource.isEmpty())
            throw new IllegalArgumentException("Resource cannot be empty!");

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

    /**
     * Open the license file
     *
     * @param translationBundle The {@link ResourceBundle} object that contains translations
     */
    public void openLicenseFile(final ResourceBundle translationBundle) {
        logger.info("Attempting to open the license file");

        try {
            openFileFromResources(new RunnableFileOpener(SharedVariables.LICENSE_FILE_LOCATION, new IRunnableHelper() {
                @Override
                public void executed() {
                    Platform.runLater(() -> logger.info("Successfully opened the license file"));
                }

                @Override
                public void exceptionOccurred(final Exception ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logger.error("Error opening the license file", ex);
                            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                        }
                    });

                }
            }), SharedVariables.LICENSE_RESOURCE_LOCATION);
        } catch (final IOException ex) {
            logger.error("Error opening the license file", ex);
            FxUtils.showErrorAlert(translationBundle.getString("LicenseFileError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
        }
    }

    /**
     * Open a website
     *
     * @param url            The URL of the website
     * @param resourceBundle The {@link ResourceBundle} object that contains translations
     */
    public void openWebsite(final String url, final ResourceBundle resourceBundle) {
        if (url == null)
            throw new NullPointerException("URL cannot be null!");
        if (url.isEmpty())
            throw new IllegalArgumentException("URL cannot be empty!");

        logger.info("Opening the website {}", url);

        final RunnableSiteOpener runnableSiteOpener = new RunnableSiteOpener(url, new IRunnableHelper() {
            @Override
            public void executed() {
                Platform.runLater(() -> logger.info("Successfully opened website"));
            }

            @Override
            public void exceptionOccurred(final Exception ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        logger.error("Error opening the website {}", url, ex);
                        FxUtils.showErrorAlert(resourceBundle.getString("WebsiteError"), ex.toString(), getClass().getResourceAsStream(SharedVariables.ICON_URL));
                    }
                });
            }
        });

        new Thread(runnableSiteOpener).start();
    }
}
