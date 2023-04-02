module Opal {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    //noinspection Java9RedundantRequiresStatement
    requires jdk.crypto.ec; // Added for SSL handshakes
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires atlantafx.base;

    opens com.codedead.opal.controller to javafx.fxml;
    opens com.codedead.opal.domain to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.codedead.opal;
}
