module Opal {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.compiler;
    requires java.naming;
    requires javafx.media;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires atlantafx.base;

    opens com.codedead.opal.controller to javafx.fxml;
    opens com.codedead.opal.domain to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.codedead.opal;
}
