package com.frontend.gui;

import java.io.File;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProfileController extends BaseController {
    private static final Logger logger = Logger.getLogger(ProfileController.class.getName());
    
    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private Label followersCount;
    @FXML private Label followingCount;
    @FXML private ListView<String> favoriteSongsList;
    @FXML private ListView<String> libraryList;
    @FXML private ListView<String> friendsList;
    @FXML private ImageView profileImage;

    @FXML
    public void initialize() {
        super.initialize();
        
        // Check if user is logged in through the facade
        if (facade.isLoggedIn()) {
            // Set user info using facade methods
            usernameLabel.setText(facade.getLoggedInUsername());
            
            // Use facade to get bio or default
            String bio = facade.getUserBio();
            bioLabel.setText(bio != null && !bio.isEmpty() ? bio : "I am music");
            
            // Follow stats
            followingCount.setText(String.valueOf(facade.getFollowedUsernames().size()));
            followersCount.setText("0"); // Placeholder for future implementation
            
            // Load profile picture
            loadProfilePicture(facade.getUserProfilePicturePath());

            // Lists
            friendsList.getItems().addAll(facade.getFollowedUsernames());
            favoriteSongsList.getItems().addAll(facade.getFavoriteSongTitles());
            libraryList.getItems().addAll(facade.getFavoriteSongTitles()); // Placeholder
        } else {
            logger.warning("No user is logged in. Cannot load user data.");
        }
    }
    
    /**
     * Loads the profile picture from the given path
     */
    private void loadProfilePicture(String picturePath) {
        // If path is empty or null, use default picture
        if (picturePath == null || picturePath.isEmpty()) {
            loadDefaultProfilePicture();
            return;
        }
        
        try {
            // Try loading from absolute path
            File file = new File(picturePath);
            if (file.exists()) {
                profileImage.setImage(new Image(file.toURI().toString()));
                return;
            }
            
            // If that fails, load the default picture
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
                profileImage.setImage(new Image(defaultProfileFile.toURI().toString()));
                return;
            }
            
            // If all else fails, log that we couldn't load the default image
            logger.warning("Could not load default profile picture");
        } catch (Exception e) {
            logger.warning("Error loading default profile picture: " + e.getMessage());
        }
    }
}
