package com.frontend.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.model.MusicAppFacade;
import com.model.Song;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class DiscoverController extends BaseController {
    @FXML private Button       toggleGenreBtn;
    @FXML private FlowPane     genrePane;
    @FXML private TextField    searchField;
    @FXML private ListView<Song> songListView;

    private MusicAppFacade facade;
    private ObservableList<Song> masterList;

    // hard-coded genres
    private static final List<String> GENRES = Arrays.asList(
        "Rock", "Pop", "Electronic", "Hip-Hop", "R&B", "Indie", "Jazz", "Classical"
    );

    @FXML @Override
    public void initialize() {
        super.initialize();
        facade = MusicAppFacade.getInstance();

        // 1) master list
        masterList = FXCollections.observableArrayList(
            facade.getSongList().getSongs()
        );
        songListView.setCellFactory(lv -> new SongCell());

        // 2) build genre toggle-buttons
        for (String genre : GENRES) {
            ToggleButton btn = new ToggleButton(genre);
            btn.getStyleClass().add("genre-toggle");
            btn.setOnAction(e -> refreshList());
            genrePane.getChildren().add(btn);
        }

        // 3) wire up search field
        searchField.textProperty().addListener((obs, o, n) -> refreshList());

        // 4) genre-pane toggle button
        toggleGenreBtn.setOnAction(e -> {
            boolean showing = genrePane.isVisible();
            genrePane.setVisible(!showing);
            genrePane.setManaged(!showing);
            toggleGenreBtn.setText(showing ? "Show Genre Filters" : "Hide Genre Filters");
        });

        // 5) initial state: genres shown
        genrePane.setVisible(false);
        genrePane.setManaged(false);
        toggleGenreBtn.setText("Hide Genre Filters");

        // 6) first fill
        refreshList();
    }

    private void refreshList() {
        // collect selected genres
        List<String> sel = genrePane.getChildren().stream()
            .map(n -> (ToggleButton)n)
            .filter(ToggleButton::isSelected)
            .map(ToggleButton::getText)
            .collect(Collectors.toList());

        // union‐filter by genre
        List<Song> byGenre;
        if (sel.isEmpty()) {
            byGenre = new ArrayList<>(masterList);
        } else {
            Set<Song> set = new LinkedHashSet<>();
            for (String g : sel) {
                set.addAll(facade.filterByGenre(g));
            }
            byGenre = new ArrayList<>(set);
        }

        // apply search on top
        String q = searchField.getText();
        List<Song> finalList = (q == null || q.isBlank())
            ? byGenre
            : byGenre.stream()
                     .filter(s -> s.matchesQuery(q.trim()))
                     .collect(Collectors.toList());

        songListView.setItems(FXCollections.observableArrayList(finalList));
    }

    private class SongCell extends ListCell<Song> {
        private final HBox content;
        private final Label titleLabel, composerLabel, publisherLabel;
        private final Region spacer;
        private final ToggleButton favBtn;

        SongCell() {
            titleLabel     = new Label();
            composerLabel  = new Label();
            publisherLabel = new Label();
            spacer         = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // favorite toggle
            favBtn = new ToggleButton("♡");
            favBtn.getStyleClass().add("favorite-button");
            favBtn.selectedProperty().addListener((obs, was, now) -> {
                Song s = getItem();
                if (s != null) {
                    if (now) {
                        facade.addFavoriteSong(s);
                        favBtn.setText("♥");
                    } else {
                        facade.removeFavoriteSong(s);
                        favBtn.setText("♡");
                    }
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

            setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !isEmpty()) {
                    Song song = getItem();
                    facade.setViewedSong(song);
                    Platform.runLater(() -> {
                        navigateTo(ViewConstants.SONG_VIEW);
                        Platform.runLater(SongController::redrawActiveView);
                    });
                }
            });
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
                    song.getPublisher() != null
                      ? "(" + song.getPublisher().getUsername() + ")"
                      : ""
                );

                boolean isFav = facade.getUser() != null
                             && facade.getUser().getFavoriteSongs().contains(song);
                favBtn.setSelected(isFav);
                favBtn.setText(isFav ? "♥" : "♡");

                setGraphic(content);
            }
        }
    }
}
