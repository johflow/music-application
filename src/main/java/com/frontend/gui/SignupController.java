package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import java.util.logging.Logger;
import com.model.AuthResult;

public class SignupController extends BaseController {
    private static final Logger logger = Logger.getLogger(SignupController.class.getName());
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    
    @FXML
    public void initialize() {
        super.initialize();
        try {
            errorLabel.setVisible(false);
        } catch (Exception e) {
            logger.severe("Error initializing SignupController: " + e.getMessage());
            errorLabel.setText("Error initializing application");
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleSignup() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        try {
            // Validate email and password through facade before registration
            facade.validateEmail(email);
            facade.validatePassword(password);
            
            // Register user after validation
            facade.register(username, password, email);
            errorLabel.setVisible(false);
            
            // Login the user automatically after registration
            AuthResult loginResult = facade.login(username, password);
            if (loginResult == AuthResult.SUCCESS) {
                updateNavigationVisibility();
                navigateTo(ViewConstants.DISCOVER_VIEW);
            } else {
                navigateToLogin();
            }
        } catch (IllegalArgumentException e) {
            // This will catch validation errors (email, password, username taken)
            logger.warning("Validation error during registration: " + e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            // This will catch any other unexpected errors
            logger.severe("Error during registration: " + e.getMessage());
            showError("Error during registration: " + e.getMessage());
        }
    }
    
    @FXML
    private void navigateToLogin() {
        try {
            navigateTo(ViewConstants.LOGIN_VIEW);
        } catch (Exception e) {
            logger.severe("Error navigating to login page: " + e.getMessage());
            errorLabel.setText("Error navigating to login page");
            errorLabel.setVisible(true);
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 