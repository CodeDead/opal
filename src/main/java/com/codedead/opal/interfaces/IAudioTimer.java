package com.codedead.opal.interfaces;

import com.codedead.opal.controller.MainWindowController;

public sealed interface IAudioTimer permits MainWindowController {

    void fired();

    void cancelled();

}
