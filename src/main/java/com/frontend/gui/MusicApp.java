package com.frontend.gui;

import java.io.IOException;
import java.util.logging.Logger;

import com.model.MusicAppFacade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MusicApp extends Application {
    private static final Logger logger = Logger.getLogger(MusicApp.class.getName());
    private static BorderPane rootLayout;
    private static BaseController baseController;
    private static BaseStyleManager styleManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize style manager
            styleManager = BaseStyleManager.getInstance();
            
            // Load the root layout
            initRootLayout();
            
            // Add the login view to the root layout
            showLoginView();
        
            // Create scene and apply theme
            Scene scene = new Scene(rootLayout);
            
            // Set theme based on user preference or default
            MusicAppFacade facade = MusicAppFacade.getInstance();
            styleManager.setCurrentTheme(facade.getDefaultTheme());
            
            // Apply theme to scene
            styleManager.applyTheme(scene);
            
            // Set up the stage
            primaryStage.setTitle("NoteStack™");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
        } catch (Exception e) {
            logger.severe("Error starting application: " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private void howLoginView() {
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
    
    public static BaseStyleManager getStyleManager() {
        return styleManager;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}