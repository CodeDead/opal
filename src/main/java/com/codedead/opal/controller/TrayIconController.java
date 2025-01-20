package com.codedead.opal.controller;

import com.codedead.opal.domain.ObservableResourceFactory;
import com.codedead.opal.interfaces.TrayIconListener;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public final class TrayIconController {

    private TrayIcon trayIcon;
    private final ObservableResourceFactory resourceFactory;
    private final TrayIconListener trayIconListener;
    private final Logger logger;

    /**
     * Initialize a new TrayIconController
     *
     * @param resourceFactory  The {@link ObservableResourceFactory} object
     * @param trayIconListener The {@link TrayIconListener} interface
     */
    public TrayIconController(final ObservableResourceFactory resourceFactory, final TrayIconListener trayIconListener) {
        if (resourceFactory == null)
            throw new NullPointerException("ResourceFactory cannot be null!");
        if (trayIconListener == null)
            throw new NullPointerException("TrayIconListener cannot be null!");

        this.resourceFactory = resourceFactory;
        this.trayIconListener = trayIconListener;
        this.logger = LogManager.getLogger(TrayIconController.class);
    }

    /**
     * Create a tray icon
     *
     * @throws IOException When the {@link TrayIcon} could not be created
     */
    private void createTrayIcon() throws IOException {
        logger.info("Creating tray icon");
        if (!SystemTray.isSupported()) {
            logger.warn("SystemTray is not supported");
            return;
        }

        final SystemTray tray = SystemTray.getSystemTray();
        final Dimension trayIconSize = tray.getTrayIconSize();
        final PopupMenu popup = new PopupMenu();
        final BufferedImage trayIconImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/opal.png")));
        final TrayIcon localTrayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconSize.width, trayIconSize.height, java.awt.Image.SCALE_SMOOTH));
        final java.awt.MenuItem displayItem = new java.awt.MenuItem(resourceFactory.getResources().getString("Display"));
        final java.awt.MenuItem settingsItem = new java.awt.MenuItem(resourceFactory.getResources().getString("Settings"));
        final java.awt.MenuItem aboutItem = new java.awt.MenuItem(resourceFactory.getResources().getString("About"));
        final java.awt.MenuItem exitItem = new java.awt.MenuItem(resourceFactory.getResources().getString("Exit"));

        if (trayIconListener != null) {
            // Platform.runLater to run on the JavaFX thread
            displayItem.addActionListener(_ -> Platform.runLater(trayIconListener::onShowHide));
            settingsItem.addActionListener(_ -> Platform.runLater(trayIconListener::onSettings));
            aboutItem.addActionListener(_ -> Platform.runLater(trayIconListener::onAbout));
            exitItem.addActionListener(_ -> Platform.runLater(trayIconListener::onExit));

            localTrayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        Platform.runLater(trayIconListener::onShowHide);
                    }
                }
            });
        }

        popup.add(displayItem);
        popup.addSeparator();
        popup.add(settingsItem);
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(exitItem);

        localTrayIcon.setToolTip("Opal");
        localTrayIcon.setPopupMenu(popup);

        this.trayIcon = localTrayIcon;
    }

    /**
     * Display the tray icon
     *
     * @throws IOException When the {@link TrayIcon} could not be created
     */
    public void showTrayIcon() throws IOException {
        logger.info("Displaying tray icon");
        if (trayIcon == null) {
            createTrayIcon();
            if (invalidTrayIcon()) {
                return;
            }
        }

        final SystemTray tray = SystemTray.getSystemTray();
        try {
            if (!Arrays.asList(tray.getTrayIcons()).contains(trayIcon)) {
                tray.add(trayIcon);
            }
        } catch (final AWTException e) {
            logger.error("TrayIcon could not be added", e);
        }
    }

    /**
     * Hide the tray icon
     */
    public void hideTrayIcon() {
        logger.info("Hiding tray icon");

        if (invalidTrayIcon()) {
            return;
        }

        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);

        trayIcon = null;
    }

    /**
     * Check if the tray icon is valid
     *
     * @return True if the tray icon is valid, false otherwise
     */
    private boolean invalidTrayIcon() {
        if (trayIcon == null) {
            logger.warn("TrayIcon cannot be null!");
            return true;
        }
        return false;
    }
}
