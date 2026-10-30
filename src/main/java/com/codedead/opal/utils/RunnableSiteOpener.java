package com.codedead.opal.utils;

import com.codedead.opal.interfaces.IRunnableHelper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// Unfortunately, this class is needed to circumvent Linux errors involving the Desktop class and browsing websites
// See https://stackoverflow.com/a/27880944 for more information
public final class RunnableSiteOpener implements Runnable {

    private final String url;
    private final IRunnableHelper iRunnableHelper;

    /**
     * Initialize a new RunnableSiteOpener
     *
     * @param url             The URL that should be opened
     * @param iRunnableHelper The {@link IRunnableHelper} interface that can be used to delegate messages
     */
    public RunnableSiteOpener(final String url, final IRunnableHelper iRunnableHelper) {
        if (url == null)
            throw new NullPointerException("URL cannot be null!");
        if (url.isBlank())
            throw new IllegalArgumentException("URL cannot be blank!");

        this.url = url;
        this.iRunnableHelper = iRunnableHelper;
    }

    /**
     * Method that is invoked to run the task
     */
    @Override
    public void run() {

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                final String os = System.getProperty("os.name").toLowerCase();

                final String[] command = getCommand(os);

                //noinspection EmptyTryBlock
                try (final Process process = new ProcessBuilder(command)
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start()) {
                }
            }
            if (iRunnableHelper != null) {
                iRunnableHelper.executed();
            }
        } catch (final IOException | URISyntaxException | UnsupportedOperationException ex) {
            if (iRunnableHelper != null) {
                iRunnableHelper.exceptionOccurred(ex);
            }
        }
    }

    /**
     * Get the command for opening a URL
     *
     * @param os The operating system
     * @return The command for opening a URL
     */
    private String [] getCommand(final String os) {
        final String[] command;
        if (os.contains("win")) {
            command = new String[]{"rundll32", "url.dll,FileProtocolHandler", url};
        } else if (os.contains("mac")) {
            command = new String[]{"open", url};
        } else if (os.contains("nix") || os.contains("nux")) {
            command = new String[]{"xdg-open", url};
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
        return command;
    }
}
