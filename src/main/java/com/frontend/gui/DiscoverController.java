// DiscoverController.java
package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
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
    @FXML private ListView<Song> songListView;
    private MusicAppFacade facade;

    @FXML @Override
    public void initialize() {
        super.initialize();
        facade = MusicAppFacade.getInstance();

        ObservableList<Song> songs =
            FXCollections.observableArrayList(facade.getSongList().getSongs());
        songListView.setItems(songs);
        songListView.setCellFactory(lv -> new SongCell());
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

            // ❤️/♡ toggle
            favBtn = new ToggleButton("♡");
            favBtn.getStyleClass().add("favorite-button");

            // when toggled
            favBtn.selectedProperty().addListener((obs, wasSel, isNowSel) -> {
                Song song = getItem();
                if (song != null) {
                    if (isNowSel) {
                        facade.addFavoriteSong(song);      // add to facade’s favorites :contentReference[oaicite:6]{index=6}&#8203;:contentReference[oaicite:7]{index=7}
                        favBtn.setText("♥");
                    } else {
                        facade.removeFavoriteSong(song);  // remove from facade’s favorites :contentReference[oaicite:8]{index=8}&#8203;:contentReference[oaicite:9]{index=9}
                        favBtn.setText("♡");
                    }
                }
            });

            // double‑click row still opens edit
            this.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
                if (evt.getClickCount() == 2 && !isEmpty()) {
                    Song song = getItem();
                    facade.setViewedSong(song);
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
                    song.getPublisher()!=null
                      ? "(" + song.getPublisher().getUsername() + ")"
                      : ""
                );

                // initialize favorite state from current user’s favorites :contentReference[oaicite:10]{index=10}&#8203;:contentReference[oaicite:11]{index=11}
                boolean isFav = facade.getUser() != null
                             && facade.getUser().getFavoriteSongs().contains(song);
                favBtn.setSelected(isFav);
                favBtn.setText(isFav ? "♥" : "♡");

                setGraphic(content);
            }
        }
    }
}
