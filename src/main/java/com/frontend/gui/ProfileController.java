// ProfileController.java
package com.frontend.gui;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.model.User;
import com.model.UserList;

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
    @FXML private Button addFriendButton;
    @FXML private Button removeFriendButton;

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
            followersCount.setText(String.valueOf(UserList.getInstance().getFollowers(facade.getUser()).size()));

            // Open a popup showing followers when followersCount is clicked
            followersCount.setOnMouseClicked(event -> {
                List<String> followers = UserList.getInstance().getFollowers(facade.getUser());

                Stage popupStage = new Stage();
                popupStage.setTitle("Followers");

                ListView<String> followersView = new ListView<>();
                followersView.getItems().setAll(followers);
                followersView.setPrefWidth(250);
                followersView.setPrefHeight(300);

                VBox layout = new VBox(followersView);
                layout.setPadding(new Insets(10));
                Scene scene = new Scene(layout);

                popupStage.setScene(scene);
                popupStage.initOwner(followersCount.getScene().getWindow());
                popupStage.show();
            });

            // Load user's profile picture
            loadProfilePicture(facade.getUserProfilePicturePath());

            friendsList.getItems().setAll(facade.getFollowedUsernames());
            favoriteSongsList.getItems().setAll(facade.getFavoriteSongTitles());
            libraryList.getItems().setAll(facade.getCreatedSongTitles());

            // Set up right-click menu for unfollowing friends
            setupFriendListContextMenu();
        } else {
            logger.warning("No user is logged in. Cannot load user data.");
        }
    }

    private void loadProfilePicture(String picturePath) {
        Image img = null;
        // Try user's image
        if (picturePath != null && !picturePath.isEmpty()) {
            // Try to load from relative path first
            if (picturePath.startsWith(ViewConstants.PROFILE_IMAGES_PATH)) {
                String relativePath = "src/main/resources" + picturePath;
                File file = new File(relativePath);
                if (file.exists()) {
                    img = new Image(file.toURI().toString());
                }
            } else {
                // Try as absolute path
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

    private void setupFriendListContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem unfollowItem = new MenuItem("Unfollow");

        unfollowItem.setOnAction(event -> {
            String selectedFriend = friendsList.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                User selectedUser = UserList.getInstance().getUser(selectedFriend);
                if (selectedUser != null) {
                    facade.unfollowUser(selectedUser);
                    facade.save();
                    refreshFriendsList();
                    friendsList.getSelectionModel().clearSelection();
                    logger.info("Unfollowed: " + selectedFriend);
                }
            }
        });

        contextMenu.getItems().add(unfollowItem);
        friendsList.setContextMenu(contextMenu);
    }

    @FXML
    private void handleAddFriend() {
        List<String> availableUsers = UserList.getInstance().getAllUsers().stream()
            .map(User::getUsername)
            .filter(username -> !username.equals(facade.getLoggedInUsername()))
            .filter(username -> !facade.getFollowedUsernames().contains(username))
            .collect(Collectors.toList());

        if (availableUsers.isEmpty()) {
            logger.info("No users available to follow.");
            return;
        }

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Select a Friend to Add");

        ListView<String> userListView = new ListView<>();
        userListView.getItems().addAll(availableUsers);

        userListView.setOnMouseClicked(event -> {
            String selectedUsername = userListView.getSelectionModel().getSelectedItem();
            if (selectedUsername != null) {
                User selectedUser = UserList.getInstance().getUser(selectedUsername);
                if (selectedUser != null) {
                    facade.followUser(selectedUser);
                    facade.save();
                    refreshFriendsList();
                    popupStage.close();
                    logger.info("Now following: " + selectedUsername);
                }
            }
        });

        VBox layout = new VBox(userListView);
        layout.setSpacing(10);
        layout.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(layout, 300, 400);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void refreshFriendsList() {
        friendsList.getItems().setAll(facade.getFollowedUsernames());
        followingCount.setText(String.valueOf(facade.getFollowedUsernames().size()));
        followersCount.setText(String.valueOf(UserList.getInstance().getFollowers(facade.getUser()).size()));
    }
}
