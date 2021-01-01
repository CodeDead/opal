module Opal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;
    requires org.apache.logging.log4j;

    opens com.codedead.opal.controller to javafx.fxml;
    opens com.codedead.opal.domain to javafx.fxml;

    exports com.codedead.opal;
}
