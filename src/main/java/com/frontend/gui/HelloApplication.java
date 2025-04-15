package com.frontend.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
// https://jenkov.com/tutorials/javafx/fxml.html documentation for FXML
public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main FXML layout that contains the contentArea StackPane and buttons
        Parent root = FXMLLoader.load(getClass().getResource("/com/frontend/gui/main.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Dynamic Content Loading");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}