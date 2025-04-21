package com.frontend.gui;

import java.io.IOException;
import java.util.logging.Logger;

import com.model.MusicAppFacade;
import com.model.ThemeColor;
import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class BaseController {
    private static final Logger logger = Logger.getLogger(BaseController.class.getName());
    
    @FXML
    protected BorderPane contentArea;
    
    @FXML protected Button discoverBtn;
    @FXML protected Button profileBtn;
    @FXML protected Button createBtn;
    @FXML protected Button settingsBtn;
    @FXML protected HBox navBar;
    
    protected MusicAppFacade facade;
    protected BaseStyleManager styleManager;
    
    public BorderPane getContentArea() {
        if (contentArea == null) {
            BaseController baseController = MusicApp.getBaseController();
            if (baseController != null) {
                contentArea = baseController.contentArea;
            }
        }
        return contentArea;
    }
    
    @FXML
    public void initialize() {
        try {
            facade = MusicAppFacade.getInstance();
            styleManager = BaseStyleManager.getInstance();
            
            // Apply the current theme
            ThemeColor theme = facade.getDefaultTheme();
            try {
                // Try to get the user's preferred theme, fall back to default if there's an issue
                if (facade != null) {
                    try {
                        User user = facade.getUser();
                        if (user != null) {
                            theme = user.getThemeColor();
                        }
                    } catch (IllegalArgumentException e) {
                        // User is null, using default theme
                        logger.fine("No user logged in, using default theme");
                    }
                }
            } catch (Exception e) {
                logger.warning("Could not get user theme color, using default: " + e.getMessage());
            }
            
            styleManager.setCurrentTheme(theme);
            
            // Ensure nav buttons get proper styling classes
            setupNavButtons();
            
            updateNavigationVisibility();
        } catch (Exception e) {
            logger.severe("Error initializing BaseController: " + e.getMessage());
        }
    }
    
    /**
     * Set up navigation buttons with proper style classes
     */
    private void setupNavButtons() {
        // Add style classes to help CSS find and style the buttons
        if (discoverBtn != null) {
            if (!discoverBtn.getStyleClass().contains("nav-button")) {
                discoverBtn.getStyleClass().add("nav-button");
            }
        }
        
        if (profileBtn != null) {
            if (!profileBtn.getStyleClass().contains("nav-button")) {
                profileBtn.getStyleClass().add("nav-button");
            }
        }
        
        if (createBtn != null) {
            if (!createBtn.getStyleClass().contains("nav-button")) {
                createBtn.getStyleClass().add("nav-button");
            }
        }
        
        if (settingsBtn != null) {
            if (!settingsBtn.getStyleClass().contains("nav-button")) {
                settingsBtn.getStyleClass().add("nav-button");
            }
        }
    }
    
    /**
     * Updates the visibility of the navigation bar based on login status
     * and current view.
     */
    protected void updateNavigationVisibility() {
        // Get the base controller from MusicApp to access the actual buttons and navbar
        BaseController baseController = this;
        if (navBar == null && MusicApp.getBaseController() != null) {
            baseController = MusicApp.getBaseController();
        }

        boolean isLoggedIn = facade.getUser() != null;
        
        // Hide the entire nav bar if not logged in
        if (baseController.navBar != null) {
            baseController.navBar.setVisible(isLoggedIn);
            baseController.navBar.setManaged(isLoggedIn); // This ensures the space is not reserved when hidden
        }
        
        // Still update button visibility for controllers that may use them independently
        if (baseController.discoverBtn != null) baseController.discoverBtn.setVisible(isLoggedIn);
        if (baseController.profileBtn != null) baseController.profileBtn.setVisible(isLoggedIn);
        if (baseController.createBtn != null) baseController.createBtn.setVisible(isLoggedIn);
        if (baseController.settingsBtn != null) baseController.settingsBtn.setVisible(isLoggedIn);
    }
    
    /**
     * Navigates to the specified FXML view
     * @param fxmlPath The path to the FXML file
     */
    protected void navigateTo(String fxmlPath) {
        try {
            logger.info("Navigating to: " + fxmlPath);
            
            // First check if the resource exists
            var resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                logger.severe("FXML resource not found: " + fxmlPath);
                return;
            }
            
            // Special handling for login and signup pages - always hide nav bar
            boolean isLoginOrSignup = fxmlPath.equals(ViewConstants.LOGIN_VIEW) || 
                                    fxmlPath.equals(ViewConstants.SIGNUP_VIEW);
            
            // If navigating to login/signup, force logout
            if (isLoginOrSignup) {
                performLogout();
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            
            try {
                Node view = loader.load();
                
                BorderPane targetContentArea = getContentArea();
                if (targetContentArea == null) {
                    logger.severe("Target content area is null, cannot navigate");
                    return;
                }
                
                // Set the new content
                targetContentArea.setCenter(view);
                
                // Apply theme to the scene after navigation
                if (targetContentArea.getScene() != null) {
                    styleManager.applyTheme(targetContentArea.getScene());
                }
                
                updateNavigationVisibility();
                
            } catch (IOException e) {
                logger.severe("Error loading FXML file " + fxmlPath + ": " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.severe("Unexpected error during navigation to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    protected void handleDiscover() {
        navigateTo(ViewConstants.DISCOVER_VIEW);
    }
    
    @FXML
    protected void handleProfile() {
        navigateTo(ViewConstants.PROFILE_VIEW);
    }
    
    @FXML
    protected void handleCreateSong() {
        navigateTo(ViewConstants.CREATE_SONG_VIEW);
    }
    
    @FXML
    protected void handleSettings() {
        navigateTo(ViewConstants.SETTINGS_VIEW);
    }
    
    /**
     * Performs logout operations. 
     * This method is for use by SettingsController and other classes that need to perform a logout.
     * It does not handle navigation - that should be done by the calling class.
     */
    protected void performLogout() {
        try {
            if (facade != null) {
                facade.logout();
            }
            updateNavigationVisibility();
        } catch (Exception e) {
            logger.severe("Error during logout: " + e.getMessage());
        }
    }
}