package com.frontend.gui;

import java.util.logging.Logger;
import com.model.AuthResult;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class LoginController extends BaseController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordBtn;
    @FXML private ImageView eyeIcon;



    @FXML
    public void initialize() {
        super.initialize();
        try {
            errorLabel.setVisible(false);

            if (eyeIcon != null) {
                eyeIcon.setImage(new Image(getClass().getResource("/images/eye-show.png").toExternalForm()));
            }

            // Force navbar hidden regardless of login state
            BaseController baseController = MusicApp.getBaseController();
            if (baseController != null && baseController.navBar != null) {
                baseController.navBar.setVisible(false);
                baseController.navBar.setManaged(false);
            }
        } catch (Exception e) {
            logger.severe("Error initializing LoginController: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.isVisible()
            ? passwordField.getText()
            : visiblePasswordField.getText();


        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            errorLabel.setVisible(true);
            return;
        }
        
        try {
            // Try to login and get specific result
            AuthResult result = facade.login(username, password);
            
            switch (result) {
                case SUCCESS:
                    errorLabel.setVisible(false);
                    updateNavigationVisibility();
                    navigateTo(ViewConstants.DISCOVER_VIEW);
                    break;
                case INVALID_USERNAME:
                    errorLabel.setText("User not found");
                    errorLabel.setVisible(true);
                    break;
                case INVALID_PASSWORD:
                    errorLabel.setText("Incorrect password. Try again.");
                    errorLabel.setVisible(true);
                    break;
                default:
                    errorLabel.setText("Login failed");
                    errorLabel.setVisible(true);
                    break;
            }
        } catch (IllegalArgumentException e) {
            // Password validation error
            logger.warning("Login validation error: " + e.getMessage());
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
            // Other unexpected errors
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

    // Toggles the visibility of the password field between masked and plain text
    @FXML
    private void togglePasswordVisibility() {
        if (visiblePasswordField.isVisible()) {
            passwordField.setText(visiblePasswordField.getText());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            eyeIcon.setImage(new Image(getClass().getResource("/images/eye-show.png").toExternalForm()));
        } else {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            eyeIcon.setImage(new Image(getClass().getResource("/images/eye-unshow.png").toExternalForm()));
        }
    }

}