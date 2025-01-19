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
     * @param iRunnableHelper The {@link IRunnableHelper} interface that can be used to delegate messages
     */
    public RunnableFileOpener(final String fileLocation, final IRunnableHelper iRunnableHelper) {
        if (fileLocation == null)
            throw new NullPointerException("File location cannot be null!");
        if (fileLocation.isBlank())
            throw new IllegalArgumentException("File location cannot be blank!");

        this.fileLocation = fileLocation;
        this.iRunnableHelper = iRunnableHelper;
    }

    /**
     * Get the file location
     *
     * @return The file location
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Method that is invoked to run the task
     */
    @Override
    public void run() {
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
