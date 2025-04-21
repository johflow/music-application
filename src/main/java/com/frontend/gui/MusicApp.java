package com.frontend.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Logger;
import javafx.scene.layout.BorderPane;

public class MusicApp extends Application {
    private static final Logger logger = Logger.getLogger(MusicApp.class.getName());
    private static BorderPane rootLayout;
    private static BaseController baseController;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            initRootLayout();
            showLoginView();
            
            Scene scene = new Scene(rootLayout);
            primaryStage.setTitle("Music Application");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.severe("Error starting application: " + e.getMessage());
        }
    }
    
    private void initRootLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.BASE_LAYOUT_VIEW));
        rootLayout = loader.load();
        baseController = loader.getController();
    }
    
    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.LOGIN_VIEW));
            Parent loginView = loader.load();
            
            if (baseController != null && baseController.getContentArea() != null) {
                baseController.getContentArea().setCenter(loginView);
            } else {
                rootLayout.setCenter(loginView);
            }
        } catch (IOException e) {
            logger.severe("Error loading login view: " + e.getMessage());
        }
    }
    
    public static BaseController getBaseController() {
        return baseController;
    }
    
    public static BorderPane getRootLayout() {
        return rootLayout;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}