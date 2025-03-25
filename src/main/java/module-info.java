module com.frontend.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires jfugue;
    requires java.sql;
    requires json.simple;
  requires java.xml.crypto;

  opens com.frontend.gui to javafx.fxml;
    exports com.frontend.gui;

    opens com.model to javafx.fxml;
    exports com.model;
}