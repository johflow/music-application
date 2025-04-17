package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import com.model.MusicAppFacade;
import java.io.IOException;
import java.util.logging.Logger;

public class BaseController {
    private static final Logger logger = Logger.getLogger(BaseController.class.getName());
    
    @FXML
    protected BorderPane contentArea;
    
    @FXML protected Button discoverBtn;
    @FXML protected Button profileBtn;
    @FXML protected Button createBtn;
    @FXML protected Button settingsBtn;
    @FXML protected Button logoutBtn;
    
    protected MusicAppFacade facade;
    
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
            updateNavigationVisibility();
        } catch (Exception e) {
            logger.severe("Error initializing BaseController: " + e.getMessage());
        }
    }
    
    protected void updateNavigationVisibility() {
        boolean isLoggedIn = facade != null && facade.getUser() != null;
        
        // Get the base controller from MusicApp to access the actual buttons
        BaseController baseController = this;
        if (discoverBtn == null && MusicApp.getBaseController() != null) {
            baseController = MusicApp.getBaseController();
        }
        
        if (baseController.discoverBtn != null) baseController.discoverBtn.setVisible(isLoggedIn);
        if (baseController.profileBtn != null) baseController.profileBtn.setVisible(isLoggedIn);
        if (baseController.createBtn != null) baseController.createBtn.setVisible(isLoggedIn);
        if (baseController.settingsBtn != null) baseController.settingsBtn.setVisible(isLoggedIn);
        if (baseController.logoutBtn != null) baseController.logoutBtn.setVisible(isLoggedIn);
    }
    
    protected void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            BorderPane targetContentArea = getContentArea();
            if (targetContentArea != null) {
                targetContentArea.setCenter(view);
            } else {
                BorderPane rootLayout = MusicApp.getRootLayout();
                if (rootLayout != null) {
                    rootLayout.setCenter(view);
                }
            }
            
            // Update navigation visibility after navigation
            updateNavigationVisibility();
            
        } catch (IOException e) {
            logger.severe("Error during navigation: " + e.getMessage());
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
    
    @FXML
    protected void handleLogout() {
        try {
            if (facade != null) {
                facade.logout();
            }
            updateNavigationVisibility(); // Update visibility after logout
            navigateTo(ViewConstants.LOGIN_VIEW);
        } catch (Exception e) {
            logger.severe("Error during logout: " + e.getMessage());
        }
    }
}