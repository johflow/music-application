module music.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires json.simple;
    requires jfugue;
    requires junit;
    requires java.xml.crypto;
    requires java.logging;
    
    exports com.app;
    exports com.data;
    exports com.frontend.gui;
    exports com.model;
    exports com.service;
    
    opens com.frontend.gui to javafx.fxml;
}
