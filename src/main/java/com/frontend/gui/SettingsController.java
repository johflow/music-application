package com.frontend.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.model.ThemeColor;
import com.model.User;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsController extends BaseController {
    private static final Logger logger = Logger.getLogger(SettingsController.class.getName());
    
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label emailLabel;
    @FXML private Label themeLabel;
    @FXML private Label statusLabel;
    
    @FXML private ImageView profileImageView;
    @FXML private TextArea bioTextArea;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<ThemeColor> themeComboBox;
    
    @FXML private Button saveButton;
    @FXML private Button changePhotoButton;
    @FXML private Button signOutButton;
    
    private String currentProfilePicturePath;
    private String tempProfilePicturePath;
    
    @FXML
    public void initialize() {
        super.initialize();
        
        // Set up the theme combo box
        themeComboBox.setItems(FXCollections.observableArrayList(ThemeColor.values()));
        
        // Ensure the text area is editable
        bioTextArea.setEditable(true);
        
        // Load user information if logged in
        loadUserData();
        
        // Clear the status label
        statusLabel.setText("");
    }
    
    /**
     * Loads the current user's data into the form
     */
    private void loadUserData() {
        if (facade == null || facade.getUser() == null) {
            logger.warning("No user is logged in. Cannot load user data.");
            return;
        }
        
        User user = facade.getUser();
        
        // Set sidebar labels
        usernameLabel.setText("Username: " + user.getUsername());
        emailLabel.setText("E-Mail: " + user.getEmail());
        themeLabel.setText("Theme: " + user.getThemeColor().getColorName());
        
        // Set form fields
        bioTextArea.setText(user.getBio());
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        themeComboBox.setValue(user.getThemeColor());
        
        // Load profile picture
        currentProfilePicturePath = user.getProfilePicturePath();
        loadProfilePicture(currentProfilePicturePath);
    }
    
    /**
     * Loads the profile picture from the given path
     */
    private void loadProfilePicture(String picturePath) {
        try {
            // Check if picturePath is valid
            if (picturePath == null || picturePath.isEmpty()) {
                loadDefaultProfilePicture();
                return;
            }
            
            // Simple attempt to load from absolute path first
            try {
                File file = new File(picturePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImageView.setImage(image);
                    return;
                }
            } catch (Exception e) {
                logger.fine("Could not load image from direct path");
            }
            
            // Try user home directory next
            try {
                File file = new File(ViewConstants.USER_PROFILE_IMAGES_DIR + picturePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImageView.setImage(image);
                    return;
                }
            } catch (Exception e) {
                logger.fine("Could not load image from user directory");
            }
            
            // If all attempts fail, load the default profile picture
            loadDefaultProfilePicture();
        } catch (Exception e) {
            logger.warning("Error loading profile picture: " + e.getMessage());
            loadDefaultProfilePicture();
        }
    }
    
    /**
     * Loads the default profile picture
     */
    private void loadDefaultProfilePicture() {
        try {
            // Try the absolute path directly, which is most reliable
            String defaultPath = "src/main/resources" + ViewConstants.DEFAULT_PROFILE_IMAGE;
            File defaultProfileFile = new File(defaultPath);
            if (defaultProfileFile.exists()) {
                Image fileImage = new Image(defaultProfileFile.toURI().toString());
                profileImageView.setImage(fileImage);
                return;
            }
            
            logger.warning("Could not load default profile image from any location");
        } catch (Exception e) {
            logger.warning("Error loading default profile picture");
        }
    }
    
    /**
     * Handles clicking the "Change photo" button
     */
    @FXML
    public void handleChangePhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        // Show open dialog
        Stage stage = (Stage) changePhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                // Store the full path to the selected file
                tempProfilePicturePath = selectedFile.getAbsolutePath();
                
                // Load the selected image
                Image image = new Image(selectedFile.toURI().toString());
                profileImageView.setImage(image);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading profile picture", e);
                statusLabel.setText("Error loading profile picture: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handles clicking the "Save" button
     */
    @FXML
    public void handleSave(ActionEvent event) {
        if (facade == null || facade.getUser() == null) {
            statusLabel.setText("Not logged in. Cannot save settings.");
            return;
        }
        
        boolean anyChanges = false;
        
        // Update theme if changed
        if (!themeComboBox.getValue().equals(facade.getUser().getThemeColor())) {
            if (facade.updateThemeColor(themeComboBox.getValue())) {
                anyChanges = true;
                themeLabel.setText("Theme: " + themeComboBox.getValue().getColorName());
                
                // Apply theme on save
                styleManager.setCurrentTheme(themeComboBox.getValue());
                if (getContentArea() != null && getContentArea().getScene() != null) {
                    styleManager.applyTheme(getContentArea().getScene());
                }
            } else {
                statusLabel.setText("Failed to update theme.");
                return;
            }
        }
        
        // Validate and update username if changed
        String newUsername = usernameField.getText().trim();
        if (!newUsername.equals(facade.getUser().getUsername())) {
            if (newUsername.isEmpty()) {
                statusLabel.setText("Username cannot be empty.");
                return;
            }
            
            if (facade.updateUsername(newUsername)) {
                anyChanges = true;
                usernameLabel.setText("Username: " + newUsername);
            } else {
                statusLabel.setText("Failed to update username.");
                return;
            }
        }
        
        // Validate and update email if changed
        String newEmail = emailField.getText().trim();
        if (!newEmail.equals(facade.getUser().getEmail())) {
            if (newEmail.isEmpty()) {
                statusLabel.setText("Email cannot be empty.");
                return;
            }
            
            try {
                facade.validateEmail(newEmail);
                if (facade.updateEmail(newEmail)) {
                    anyChanges = true;
                    emailLabel.setText("E-Mail: " + newEmail);
                } else {
                    statusLabel.setText("Failed to update email.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                statusLabel.setText("Invalid email format: " + e.getMessage());
                return;
            }
        }
        
        // Validate and update password if changed
        String newPassword = passwordField.getText();
        if (!newPassword.isEmpty()) {
            try {
                facade.validatePassword(newPassword);
                if (facade.updatePassword(newPassword)) {
                    anyChanges = true;
                    passwordField.clear();
                } else {
                    statusLabel.setText("Failed to update password.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                statusLabel.setText("Invalid password: " + e.getMessage());
                return;
            }
        }
        
        // Update bio if changed
        String newBio = bioTextArea.getText();
        if (!newBio.equals(facade.getUser().getBio())) {
            if (facade.updateBio(newBio)) {
                anyChanges = true;
            } else {
                statusLabel.setText("Failed to update bio.");
                return;
            }
        }
        
        // Update profile picture if changed
        if (tempProfilePicturePath != null) {
            try {
                // Create profile images directory if it doesn't exist
                File profileImagesDir = new File(ViewConstants.USER_PROFILE_IMAGES_DIR);
                if (!profileImagesDir.exists()) {
                    profileImagesDir.mkdirs();
                }
                
                // Get the source file
                File tempFile = new File(tempProfilePicturePath);
                if (!tempFile.exists()) {
                    statusLabel.setText("Source profile picture file no longer exists.");
                    return;
                }
                
                // Get file extension
                String fileName = tempFile.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.'));
                
                // Create filename with username and sequential numbering
                String username = facade.getUser().getUsername();
                
                // Get the profile pic count for this user (to determine next number)
                int profilePicNumber = 1; // Start with 1 if no existing pics
                
                // Scan for existing profile pictures with the same username prefix
                File[] existingProfilePics = profileImagesDir.listFiles((dir, name) -> 
                    name.startsWith(username + "_") && (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")));
                
                if (existingProfilePics != null && existingProfilePics.length > 0) {
                    // Find the highest number used
                    for (File pic : existingProfilePics) {
                        String picName = pic.getName();
                        try {
                            // Extract number from username_X.ext format
                            int underscoreIndex = picName.indexOf('_');
                            int dotIndex = picName.lastIndexOf('.');
                            if (underscoreIndex != -1 && dotIndex != -1) {
                                String numberStr = picName.substring(underscoreIndex + 1, dotIndex);
                                int number = Integer.parseInt(numberStr);
                                if (number >= profilePicNumber) {
                                    profilePicNumber = number + 1;
                                }
                            }
                        } catch (NumberFormatException e) {
                            // If format is invalid, just continue
                            logger.warning("Invalid profile picture filename format: " + picName);
                        }
                    }
                }
                
                // Create the new profile picture filename
                String newFileName = username + "_" + profilePicNumber + extension;
                File targetFile = new File(ViewConstants.USER_PROFILE_IMAGES_DIR + newFileName);
                
                // Copy the file
                Files.copy(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Update the user's profile picture path
                if (facade.updateProfilePicture(targetFile.getAbsolutePath())) {
                    currentProfilePicturePath = targetFile.getAbsolutePath();
                    tempProfilePicturePath = null;
                    anyChanges = true;
                } else {
                    statusLabel.setText("Failed to update profile picture in user data.");
                    return;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error saving profile picture", e);
                statusLabel.setText("Error saving profile picture: " + e.getMessage());
                return;
            }
        }
        
        // Save changes if any were made
        if (anyChanges) {
            if (facade.saveUserSettings()) {
                statusLabel.setText("Settings saved successfully!");
            } else {
                statusLabel.setText("Error saving settings.");
            }
        } else {
            statusLabel.setText("No changes to save.");
        }
    }
    
    /**
     * Handles clicking the "Sign Out" button
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Perform the logout operation
            performLogout();
            
            // Navigate to login view
            navigateTo(ViewConstants.LOGIN_VIEW);
            
            // Display success message if needed
            logger.info("User logged out successfully");
        } catch (Exception e) {
            statusLabel.setText("Error during logout: " + e.getMessage());
            logger.severe("Error during logout: " + e.getMessage());
        }
    }
} 