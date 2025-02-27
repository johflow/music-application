module com.frontend.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires simple.json;
    requires jfugue;


    opens com.frontend.gui to javafx.fxml;
    exports com.frontend.gui;

    opens com.model to javafx.fxml;
    exports com.model;
}