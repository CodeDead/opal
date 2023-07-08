package com.codedead.opal.controller;

import atlantafx.base.theme.*;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ThemeController {

    private static final Logger logger = LogManager.getLogger(ThemeController.class);

    /**
     * Initialize a new ThemeController
     */
    private ThemeController() {
        // Default constructor
    }

    /**
     * Change the application theme
     *
     * @param themeName The theme name
     */
    public static void setTheme(final String themeName) {
        logger.info("Setting theme to {}", themeName);

        switch (themeName.toLowerCase()) {
            case "modena" -> Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
            case "caspian" -> Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
            case "nordlight" -> Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
            case "norddark" -> Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
            case "dark" -> Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            case "cupertinodark" -> Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
            case "cuptertinolight" -> Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
            case "dracula" -> Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
            default -> Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
    }

    /**
     * Get the theme index from the theme name
     *
     * @param themeName The theme name
     * @return The theme index
     */
    public static int getThemeIndex(final String themeName) {
        return switch (themeName.toLowerCase()) {
            case "cupertinodark" -> 0;
            case "cupertinolight" -> 1;
            case "dracula" -> 2;
            case "dark" -> 4;
            case "nordlight" -> 5;
            case "norddark" -> 6;
            case "modena" -> 7;
            case "caspian" -> 8;
            default -> 3;
        };
    }
}
