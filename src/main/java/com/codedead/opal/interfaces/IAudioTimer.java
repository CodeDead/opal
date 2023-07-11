package com.codedead.opal.interfaces;

public sealed interface IAudioTimer permits com.codedead.opal.controller.MainWindowController {

    void fired();

    void cancelled();

}
