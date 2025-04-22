// DiscoverController.java
package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.input.MouseEvent;

import com.model.MusicAppFacade;
import com.model.Song;

public class DiscoverController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ListView<Song> songListView;

    private MusicAppFacade facade;
    private ObservableList<Song> allSongs;

    @FXML @Override
    public void initialize() {
        super.initialize();
        facade = MusicAppFacade.getInstance();

        // 1) grab and wrap the full list
        allSongs = FXCollections.observableArrayList(facade.getSongList().getSongs());
        songListView.setItems(allSongs);
        songListView.setCellFactory(lv -> new SongCell());

        // 2) live‑search without mutating facade.songList
        searchField.textProperty().addListener((obs, oldQ, newQ) -> {
            if (newQ == null || newQ.isBlank()) {
                songListView.setItems(allSongs);
            } else {
                songListView.setItems(
                  FXCollections.observableArrayList(
                    facade.searchForSongs(newQ.trim())
                  )
                );
            }
        });
    }

    private class SongCell extends ListCell<Song> {
        private final HBox content;
        private final Label titleLabel, composerLabel, publisherLabel;
        private final Region spacer;
        private final ToggleButton favBtn;

        public SongCell() {
            titleLabel     = new Label();
            composerLabel  = new Label();
            publisherLabel = new Label();
            spacer         = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // favorite toggle, off by default
            favBtn = new ToggleButton("♡");
            favBtn.getStyleClass().add("favorite-button");
            favBtn.selectedProperty().addListener((obs, was, now) -> {
                Song song = getItem();
                if (song == null) return;
                if (now) {
                    facade.addFavoriteSong(song);
                    favBtn.setText("♥");
                } else {
                    facade.removeFavoriteSong(song);
                    favBtn.setText("♡");
                }
            });

            // double‑click to edit
            addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getClickCount() == 2 && !isEmpty()) {
                    Song s = getItem();
                    facade.setViewedSong(s);
                    navigateTo(ViewConstants.CREATE_SONG_VIEW);
                }
            });

            content = new HBox(10,
                titleLabel,
                spacer,
                composerLabel,
                publisherLabel,
                favBtn
            );
            content.getStyleClass().add("song-cell");
        }

        @Override
        protected void updateItem(Song song, boolean empty) {
            super.updateItem(song, empty);
            if (empty || song == null) {
                setGraphic(null);
            } else {
                titleLabel    .setText(song.getTitle());
                composerLabel .setText("by " + song.getComposer());
                publisherLabel.setText(
                    song.getPublisher()!=null ? "(" + song.getPublisher().getUsername() + ")" : ""
                );

                // initialize favorite state
                boolean isFav = facade.getUser()!=null
                             && facade.getUser().getFavoriteSongs().contains(song);
                favBtn.setSelected(isFav);
                favBtn.setText(isFav ? "♥" : "♡");

                setGraphic(content);
            }
        }
    }
}
