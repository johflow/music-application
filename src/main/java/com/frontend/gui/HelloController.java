package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;


public class HelloController {
    @FXML
    private StackPane contentArea;

    public void loadPage1() {
        loadContent("/com/frontend/gui/page1.fxml");
    }

    public void loadPage2() {
        loadContent("/com/frontend/gui/page2.fxml");
    }

    private void loadContent(String fxmlFile) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}