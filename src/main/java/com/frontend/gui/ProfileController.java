// ProfileController.java
package com.frontend.gui;

import java.io.File;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ProfileController extends BaseController {
    private static final Logger logger = Logger.getLogger(ProfileController.class.getName());
    private static final double PROFILE_IMG_SIZE = 110;

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

        profileImage.setFitWidth(PROFILE_IMG_SIZE);
        profileImage.setFitHeight(PROFILE_IMG_SIZE);
        profileImage.setPreserveRatio(false);

        if (facade.isLoggedIn()) {
            usernameLabel.setText(facade.getLoggedInUsername());
            String bio = facade.getUserBio();
            bioLabel.setText((bio != null && !bio.isEmpty()) ? bio : "I am music");
            followingCount.setText(String.valueOf(facade.getFollowedUsernames().size()));
            followersCount.setText("0");

            loadProfilePicture(facade.getUserProfilePicturePath());

            friendsList.getItems().setAll(facade.getFollowedUsernames());
            favoriteSongsList.getItems().setAll(facade.getFavoriteSongTitles());
            // ← UPDATED LINE:
            libraryList.getItems().setAll(facade.getCreatedSongTitles());
        } else {
            logger.warning("No user is logged in. Cannot load user data.");
        }
    }

    private void loadProfilePicture(String picturePath) {
        Image img = null;
        // Try user's image
        if (picturePath != null && !picturePath.isEmpty()) {
            File file = new File(picturePath);
            if (file.exists()) {
                img = new Image(file.toURI().toString());
            } else {
                // If the file doesn't exist at the provided path, try looking in the profiles directory
                String baseName = new File(picturePath).getName();
                File internalFile = new File(ViewConstants.USER_PROFILE_IMAGES_DIR + baseName);
                if (internalFile.exists()) {
                    img = new Image(internalFile.toURI().toString());
                }
            }
        }
        // Fallback to default if needed
        if (img == null) {
            img = loadDefaultImage();
        }

        if (img != null) {
            profileImage.setImage(img);
            applyCircularClip();
        }
    }

    private Image loadDefaultImage() {
        try {
            String defaultPath = "src/main/resources" + ViewConstants.DEFAULT_PROFILE_IMAGE;
            File defaultFile = new File(defaultPath);
            if (defaultFile.exists()) {
                return new Image(defaultFile.toURI().toString());
            }
        } catch (Exception e) {
            logger.warning("Error loading default profile picture: " + e.getMessage());
        }
        return null;
    }

    // Applies a circular clip so the image appears in a circle
    private void applyCircularClip() {
        double radius = PROFILE_IMG_SIZE / 2;
        Circle clip = new Circle(radius, radius, radius);
        profileImage.setClip(clip);
    }
}
