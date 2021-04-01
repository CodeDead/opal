package com.codedead.opal.utils;

import com.codedead.opal.interfaces.IRunnableHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public final class RunnableFileOpener implements Runnable {

    private final String fileLocation;
    private final IRunnableHelper iRunnableHelper;

    /**
     * Initialize a new RunnableFileOpener
     *
     * @param fileLocation    The location of the File that should be opened
     * @param iRunnableHelper The IRunnableHelper that can be used to delegate messages
     */
    public RunnableFileOpener(final String fileLocation, final IRunnableHelper iRunnableHelper) {
        if (fileLocation == null)
            throw new NullPointerException("File location cannot be null!");
        if (fileLocation.isEmpty())
            throw new IllegalArgumentException("File location cannot be empty!");

        this.fileLocation = fileLocation;
        this.iRunnableHelper = iRunnableHelper;
    }

    /**
     * Get the file location
     *
     * @return The file location
     */
    public final String getFileLocation() {
        return fileLocation;
    }

    @Override
    public final void run() {
        try {
            Desktop.getDesktop().open(new File(fileLocation));
            if (iRunnableHelper != null) {
                iRunnableHelper.executed();
            }
        } catch (final IOException | UnsupportedOperationException ex) {
            if (iRunnableHelper != null) {
                iRunnableHelper.exceptionOccurred(ex);
            }
        }
    }
}
