module com.frontend.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires jfugue;
    requires java.sql;
    requires json.simple;
    requires java.xml.crypto;
    requires junit;

    opens com.frontend.gui to javafx.fxml;
    opens com.model to javafx.fxml;
    opens com.service to junit;
    opens com.controllers to javafx.fxml;

    exports com.frontend.gui;
    exports com.model;
}
