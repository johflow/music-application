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
            
            // Ensure navigation buttons are properly initialized
            if (baseController != null) {
                baseController.updateNavigationVisibility();
                logger.info("Navigation buttons visibility updated in start()");
            } else {
                logger.severe("BaseController is null in start()");
            }
        } catch (Exception e) {
            logger.severe("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initRootLayout() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.BASE_LAYOUT_VIEW));
            rootLayout = loader.load();
            baseController = loader.getController();
            logger.info("Base controller initialized: " + (baseController != null));
            
            if (baseController == null) {
                logger.severe("Failed to get BaseController from FXML loader");
            } else {
                logger.info("BaseController buttons: " +
                           "discover=" + (baseController.discoverBtn != null) + 
                           ", profile=" + (baseController.profileBtn != null) + 
                           ", create=" + (baseController.createBtn != null) + 
                           ", settings=" + (baseController.settingsBtn != null));
            }
        } catch (Exception e) {
            logger.severe("Error initializing root layout: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    private void howLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.LOGIN_VIEW));
            Parent loginView = loader.load();
            
            if (baseController != null && baseController.getContentArea() != null) {
                baseController.getContentArea().setCenter(loginView);
                logger.info("Login view set in content area");
            } else {
                logger.warning("BaseController or content area is null, setting login view directly in root layout");
                rootLayout.setCenter(loginView);
            }
        } catch (IOException e) {
            logger.severe("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.CREATE_SONG_VIEW));
            Parent songView = loader.load();

            if (baseController != null && baseController.getContentArea() != null) {
                baseController.getContentArea().setCenter(songView);
                logger.info("Song view set in content area");
            } else {
                logger.warning("BaseController or content area is null, setting song view directly in root layout");
                rootLayout.setCenter(songView);
            }
        } catch (IOException e) {
            logger.severe("Error loading song view: " + e.getMessage());
            e.printStackTrace();
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