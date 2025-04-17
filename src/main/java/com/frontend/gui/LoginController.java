package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import java.util.logging.Logger;

public class LoginController extends BaseController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    @FXML
    public void initialize() {
        super.initialize();
        try {
            errorLabel.setVisible(false);
        } catch (Exception e) {
            logger.severe("Error initializing LoginController: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            errorLabel.setVisible(true);
            return;
        }
        
        try {
            if (facade.login(username, password)) {
                errorLabel.setVisible(false);
                updateNavigationVisibility();
                navigateTo(ViewConstants.DISCOVER_VIEW);
            } else {
                errorLabel.setText("Invalid username or password");
                errorLabel.setVisible(true);
            }
        } catch (Exception e) {
            logger.severe("Error during login: " + e.getMessage());
            errorLabel.setText("Error during login: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleSignup() {
        try {
            navigateTo(ViewConstants.SIGNUP_VIEW);
        } catch (Exception e) {
            logger.severe("Error navigating to signup: " + e.getMessage());
            errorLabel.setText("Error navigating to signup: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
} 