module Opal {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.compiler;
    requires java.naming;
    requires javafx.media;
    requires java.management;
    requires java.management.rmi;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires atlantafx.base;

    opens com.codedead.opal.controller to javafx.fxml;
    opens com.codedead.opal.domain to javafx.fxml, tools.jackson.databind;

    exports com.codedead.opal;
}
