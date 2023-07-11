package com.codedead.opal.interfaces;

public sealed interface TrayIconListener permits com.codedead.opal.controller.MainWindowController {

    void onShowHide();

    void onSettings();

    void onAbout();

    void onExit();
}
