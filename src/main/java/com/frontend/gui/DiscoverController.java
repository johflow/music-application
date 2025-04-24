package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import com.model.MusicAppFacade;
import com.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DiscoverController extends BaseController {
    @FXML private FlowPane      genrePane;
    @FXML private TextField     searchField;
    @FXML private ListView<Song> songListView;

    private MusicAppFacade facade;
    private ObservableList<Song> masterList;

    // hard-coded genres
    private static final List<String> GENRES = Arrays.asList(
        "Rock", "Pop", "Jazz", "Hip-Hop", "Classical", "Anime"
    );

    @FXML @Override
    public void initialize() {
        super.initialize();
        facade = MusicAppFacade.getInstance();

        // 1) load all songs once
        masterList = FXCollections.observableArrayList(
            facade.getSongList().getSongs()
        );
        songListView.setCellFactory(lv -> new SongCell());

        // 2) build a ToggleButton for each genre—no ToggleGroup, so each can stay pressed independently
        for (String genre : GENRES) {
            ToggleButton btn = new ToggleButton(genre);
            btn.getStyleClass().add("genre-toggle");
            btn.setOnAction(e -> refreshList());
            genrePane.getChildren().add(btn);
        }

        // 3) respond to search changes
        searchField.textProperty().addListener((obs, oldV, newV) -> refreshList());

        // 4) initial populate
        refreshList();
    }

    /**
     * Rebuilds the list by:
     *   a) collecting all genres whose toggle-button is {@code isSelected()}
     *   b) unioning facade.filterByGenre(...) for each
     *   c) applying the text search
     */
    private void refreshList() {
        // which genres are selected?
        List<String> selGenres = genrePane.getChildren().stream()
            .map(node -> (ToggleButton)node)
            .filter(ToggleButton::isSelected)
            .map(ToggleButton::getText)
            .collect(Collectors.toList());

        // a) filter by genres (union if multiple; all if none)
        List<Song> byGenre;
        if (selGenres.isEmpty()) {
            byGenre = new ArrayList<>(masterList);
        } else {
            Set<Song> union = new LinkedHashSet<>();
            for (String g : selGenres) {
                union.addAll(facade.filterByGenre(g));
            }
            byGenre = new ArrayList<>(union);
        }

        // b) apply search query on that subset
        String q = searchField.getText();
        List<Song> finalList;
        if (q == null || q.isBlank()) {
            finalList = byGenre;
        } else {
            String query = q.trim().toLowerCase();
            finalList = byGenre.stream()
                                .filter(s -> s.matchesQuery(query))
                                .collect(Collectors.toList());
        }

        songListView.setItems(FXCollections.observableArrayList(finalList));
    }

    /** Cell with double-click to set facade.viewedSong and navigate */
    private class SongCell extends ListCell<Song> {
        private final HBox content;
        private final Label titleLabel, composerLabel, publisherLabel;
        private final Region spacer;

        SongCell() {
            titleLabel     = new Label();
            composerLabel  = new Label();
            publisherLabel = new Label();
            spacer         = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            content = new HBox(15,
                titleLabel,
                spacer,
                composerLabel,
                publisherLabel
            );
            content.getStyleClass().add("song-cell");

            setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !isEmpty()) {
                    Song song = getItem();
                    facade.setViewedSong(song);
                    navigateTo(ViewConstants.CREATE_SONG_VIEW);
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
                setGraphic(content);
            }
        }
    }
}
