module com.frontend.gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.frontend.gui to javafx.fxml;
    exports com.frontend.gui;
}