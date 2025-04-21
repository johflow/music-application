package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.model.MusicAppFacade;

public class ProfileController extends BaseController {

    private final MusicAppFacade facade = MusicAppFacade.getInstance();

    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private Label emailLabel;
    @FXML private Label themeColorLabel;
    @FXML private Label followersCount;
    @FXML private Label followingCount;
    @FXML private ListView<String> favoriteSongsList;
    @FXML private ListView<String> libraryList;
    @FXML private ListView<String> friendsList;
    @FXML private ImageView profileImage;

    @FXML
    public void initialize() {
        super.initialize();

        // Set user info from facade
        usernameLabel.setText(facade.getLoggedInUsername());
        bioLabel.setText("I am music");
        emailLabel.setText(facade.getLoggedInEmail());
        themeColorLabel.setText(facade.getThemeColor());

        // Follow stats
        followingCount.setText(String.valueOf(facade.getFollowedUsernames().size()));
        followersCount.setText("0"); // Placeholder

        // Lists
        friendsList.getItems().addAll(facade.getFollowedUsernames());
        favoriteSongsList.getItems().addAll(facade.getFavoriteSongTitles());
        libraryList.getItems().addAll(facade.getFavoriteSongTitles()); // Placeholder

        // Profile image (optional)
        try {
            Image img = new Image(getClass().getResource("src/main/resources/com/frontend/gui/profilepic.png").toExternalForm());
            profileImage.setImage(img);
        } catch (Exception e) {
            System.out.println("Could not load profile image.");
        }
    }
}
