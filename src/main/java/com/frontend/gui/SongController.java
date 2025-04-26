package com.frontend.gui;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import com.model.Chord;
import com.model.DurationElement;
import com.model.Measure;
import com.model.MusicAppFacade;
import com.model.MusicElement;
import com.model.Note;
import com.model.Song;
import com.model.Tuplet;
import com.service.MusicXMLToSongJsonConverter;
import com.service.PlaybackTask;
import com.service.SongPlayer;
import com.model.Staff;
import com.model.User;
import com.model.SheetMusic;
import com.model.Instrument;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class SongController {
    BaseStyleManager styles = BaseStyleManager.getInstance();
    @FXML private HBox        hudBar;
    @FXML private Button      exitSongBtn;
    @FXML private Button      playSongBtn;
    @FXML private Button      importMusicXMLBtn;
    @FXML private ScrollPane  scrollPane;
    @FXML private Pane        contentPane;

    private Canvas            canvas;
    private GraphicsContext   gc;
    private Song              currentSong;
    private double width;
    private double height;
    private double measureLength;
    private double measureHeight;

    private PlaybackTask currentTask;

    @FXML private void handlePlay() {
        SongPlayer songPlayer = new SongPlayer();
        if (currentTask == null || !currentTask.isRunning()) {
            currentTask = new PlaybackTask(songPlayer.play(currentSong));
            Thread t = new Thread(currentTask, "JFugue-Playback");
            t.setDaemon(true);             // JVM can exit even if music is still playing
            t.start();
        }
    }

    @FXML private void handleStop() {       // Pause is similar: call player.pause()
        if (currentTask != null){
            currentTask.stopPlayback();
            System.out.println("Attempted stop");
        }
    }

    @FXML
    public void initialize() {
        try {
            // 1) Prepare the canvas & put it in the scrollPane
            canvas = new Canvas();
            contentPane.getChildren().add(canvas);
            scrollPane.setContent(contentPane);
            scrollPane.pannableProperty().set(true);
            gc = canvas.getGraphicsContext2D();
            gc.setStroke(styles.isDark() ? Color.WHITE : Color.BLACK);
            gc.setFill(styles.isDark() ? Color.WHITE : Color.BLACK);
            
            // 2) Load your song & figure out page count
            try {
                MusicAppFacade facade = MusicAppFacade.getInstance();
                facade.loadSongs();
                
                // Check if there's already a viewed song (selected from Discover page)
                if (facade.getViewedSong() != null) {
                    // Use the song that was selected on the discover page
                    currentSong = facade.getViewedSong();
                    System.out.println("Using selected song: " + currentSong.getTitle());
                } 
                // Handle case where there might not be any songs
                else if (facade.getSongList().getSongs().isEmpty()) {
                    System.out.println("No songs available in the library");
                    // Create an empty song to prevent NPEs
                    Song emptySong = createNewEmptySong(facade);
                    facade.setViewedSong(emptySong);
                    currentSong = emptySong;
                } else {
                    // No song specifically selected, use the first one
                    facade.setViewedSong(facade.positionToLoadedSong(0));
                    currentSong = facade.getViewedSong();
                }
            } catch (Exception e) {
                System.err.println("Error loading songs: " + e.getMessage());
                e.printStackTrace();
                // Create an empty song to prevent NPEs
                Song emptySong = createNewEmptySong(MusicAppFacade.getInstance());
                MusicAppFacade.getInstance().setViewedSong(emptySong);
                currentSong = emptySong;
            }

            // 3) Wire up HUD buttons
            playSongBtn.setOnAction(e -> {
                try {
                    MusicAppFacade.getInstance().playViewedSong();
                } catch (Exception ex) {
                    System.err.println("Error playing song: " + ex.getMessage());
                    ex.printStackTrace();
                    
                    // Show error alert
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Playback Error");
                    alert.setHeaderText(null);
                    alert.setContentText("There was an error playing the song. Please try again.");
                    alert.showAndWait();
                }
            });
            importMusicXMLBtn.setOnAction(e -> handleImportMusicXML());

            // 4) When the ScrollPane viewport appears, size & draw
            scrollPane.viewportBoundsProperty().addListener((obs,oldB,newB) -> {
                canvas.setWidth(newB.getWidth());
                canvas.setHeight(newB.getHeight());
                try {
                    redraw();
                } catch (Exception ex) {
                    System.err.println("Error redrawing canvas: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // 5) After everything lays out, do the first draw
            Platform.runLater(() -> {
                try {
                    Bounds vp = scrollPane.getViewportBounds();
                    canvas.setWidth(vp.getWidth());
                    canvas.setHeight(vp.getHeight());
                    redraw();
                } catch (Exception e) {
                    System.err.println("Error in Platform.runLater: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error initializing SongController: " + e.getMessage());
            e.printStackTrace();
            
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText(null);
            alert.setContentText("There was an error loading the song editor. Please try again.");
            alert.showAndWait();
        }
    }

    private void redraw() {
        try {
            // Check if all necessary data is available
            if (currentSong == null || currentSong.getSheetMusic() == null || currentSong.getSheetMusic().isEmpty() ||
                currentSong.getSheetMusic().get(0).getStaves() == null || currentSong.getSheetMusic().get(0).getStaves().isEmpty() ||
                currentSong.getSheetMusic().get(0).getStaves().get(0).getMeasures() == null || currentSong.getSheetMusic().get(0).getStaves().get(0).getMeasures().isEmpty()) {
                
                // Draw empty staff as fallback
                width = canvas.getWidth();
                height = canvas.getHeight();
                gc.clearRect(0, 0, width, height);
                
                // Draw simple empty staff
                double x = width/10, y = 100;
                int numOfLines = 5;
                measureLength = width / 6;
                measureHeight = height / 12;
                
                gc.strokeLine(x, y, x + measureLength, y);
                for (int i = 0; i < numOfLines; ++i) {
                    gc.strokeLine(x, y + ((measureHeight * i) / 4), x + measureLength, y + ((measureHeight * i) / 4));
                }
                gc.strokeLine(x, y, x, y + measureHeight);
                gc.strokeLine(x + measureLength, y, x + measureLength, y + measureHeight);
                
                return;
            }

            // If we get here, we know all the data is available
            int measures = currentSong.getSheetMusic().get(0).getStaves().get(0).getMeasures().size();
            int perLine = 4;
            int lines = (int) Math.ceil(measures / (double) perLine);

            measureLength = canvas.getWidth() / 6;
            measureHeight = canvas.getHeight() / 12;

            double neededH = measureHeight * 4 * lines + 2 * (height / 10);

            canvas.setHeight(neededH);
            width = canvas.getWidth();
            height = canvas.getHeight();

            System.out.println("Redrawing canvas. Width: " + width + ", Height: " + height);
            gc.clearRect(0, 0, width, height);

            // Draw the song
            drawSong(currentSong, gc, width, neededH);
        } catch (Exception e) {
            System.err.println("Error in redraw: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawSong(Song song, GraphicsContext gc, double width, double height) {
        if (song == null) {
            System.err.println("Warning: Cannot draw null song.");
            return;
        }
        
        if (gc == null) {
            System.err.println("Warning: GraphicsContext is null.");
            return;
        }
        
        try {
            if (song.getSheetMusic() == null || song.getSheetMusic().isEmpty()) {
                System.err.println("Warning: Song or sheet music is null or empty. Drawing empty staff.");
                // Draw empty staff as fallback
                double x = width/10, y = 100;
                int numOfLines = 5;
                gc.strokeLine(x, y, x + measureLength, y);
                for (int i = 0; i < numOfLines; ++i) {
                    gc.strokeLine(x, y + ((measureHeight * i) / 4), x + measureLength, y + ((measureHeight * i) / 4));
                }
                return;
            }

            double x = width/10, y = 100;

            // Draw first staff if it exists and has measures
            try {
                if (!song.getSheetMusic().get(0).getStaves().isEmpty()) {
                    Staff firstStaff = song.getSheetMusic().get(0).getStaves().get(0);
                    if (firstStaff != null && firstStaff.getMeasures() != null && !firstStaff.getMeasures().isEmpty()) {
                        for (Measure measure : firstStaff.getMeasures()) {
                            try {
                                if (measure != null) {
                                    drawMeasure(x, y, measure);
                                    x += measureLength + 1;
                                    if (x > width - measureLength) {
                                        x = width/10;
                                        y += 6 * measureHeight + 1;
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Error drawing measure: " + e.getMessage());
                            }
                        }
                    } else {
                        System.err.println("Warning: First staff is null or has null/empty measures.");
                    }
                } else {
                    System.err.println("Warning: No staves in the first sheet music.");
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Warning: Error accessing first staff: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error drawing first staff: " + e.getMessage());
            }

            // Draw the fourth staff (index 3) only if it exists
            try {
                y = 100 + measureHeight*2;
                x = width/10;
                if (song.getSheetMusic().get(0).getStaves().size() > 3) {
                    Staff fourthStaff = song.getSheetMusic().get(0).getStaves().get(3);
                    if (fourthStaff != null && fourthStaff.getMeasures() != null && !fourthStaff.getMeasures().isEmpty()) {
                        for (Measure measure : fourthStaff.getMeasures()) {
                            try {
                                if (measure != null) {
                                    drawMeasure(x, y, measure);
                                    x += measureLength + 1;
                                    if (x > width - measureLength) {
                                        x = width/10;
                                        y += 6 * measureHeight + 1;
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Error drawing measure in fourth staff: " + e.getMessage());
                            }
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Warning: Error accessing fourth staff: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error drawing fourth staff: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error drawing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawMeasure(double x, double y, Measure measure) {

        int numOfLines = 5;
        gc.strokeLine(x, y, x, y + measureHeight);
        gc.strokeLine(x + measureLength, y, x + measureLength, y + measureHeight);
        for (int i = 0; i < numOfLines; ++i) {
            gc.strokeLine(x, y + ((measureHeight * i) / 4), x + measureLength, y + ( (measureHeight * i) / 4) );
        }
        double duration = getTotalMeasureDuration(measure);
        drawMusicElements(x, y, measure.getMusicElements(), duration, measure);
    }

    private void drawMusicElements(double x, double y, List<MusicElement> musicElements, double duration, Measure measure) {
        for (MusicElement element : musicElements) {
            drawMusicElement(x, y, element);
            x += getXIncrement(element, measureLength, measure) * measureLength;
        }
    }

    private double getXIncrement(MusicElement element, double measureLength, Measure measure) {
        double duration = 0;
        if (element.getType().equals("chord")) {
            for (Note note : ((Chord) element).getNotes()) {
                if (note.getDuration() > duration) {
                    duration = note.getDuration();
                }
            }
        } else if (element.getType().equals("tuplet")) {
            for (MusicElement element1 : ((Tuplet) element).getElements()) {
                duration += getXIncrement(element1, measureLength, measure);
            }
        } else {
            duration = ((DurationElement) element).getDuration();
        }
        double test = duration / ((double) measure.getTimeSignatureNumerator() / measure.getTimeSignatureDenominator());
        return test;
    }

    private void drawMusicElement(double x, double y, MusicElement musicElement) {
        switch (musicElement.getType()) {
            case "note" -> drawNote(x, y, (Note) musicElement);
            case "chord" -> drawChord(x, y, (Chord) musicElement);
        }

    }

    private void drawChord(double x, double y, Chord musicElement) {
        for (Note note : musicElement.getNotes()) {
            drawNote(x, y, note);
        }
    }

    private void drawNote(double x, double y, Note note) {
        y += getDurationElementYPos(note, measureHeight/8);
        drawNoteHead(x, y, note);
        drawNoteStem(x, y, note);
    }

    private void drawNoteHead(double x, double y, Note note) {
        if (note.getDuration() < 0.5) {
            gc.fillOval(x, y - measureHeight/8, 3*measureHeight/8, measureHeight/4);
            return;
        }
        gc.strokeOval(x, y - measureHeight/8, 3*measureHeight/8, measureHeight/4);
    }

    private void drawNoteStem(double x, double y, Note note) {
        if (note.getDuration() == 1.0) {
            return;
        }
        if (note.getMidiNumber() > 69) {
            gc.strokeLine(x, y + 3, x, y + measureHeight * 3/4);
            return;
        }
        gc.strokeLine(x + 3*measureHeight/8, y + 3, x + 3*measureHeight/8, y - measureHeight * 3/4);
    }

    private double getTotalMeasureDuration(Measure measure) {
        return (double) measure.getTimeSignatureDenominator() / measure.getTimeSignatureNumerator();
    }

    private double getDurationElementYPos(DurationElement element, double lineOffset) {
        if (element.getType().equals("rest")) return 0; //TODO offset to B4
        int noteNumber = noteNameToInt.get(((Note) element).getNoteName().charAt(0));
        int octaveNumber = ((Note) element).getNoteName().charAt((((Note) element).getNoteName()).length() - 1) - '0';
        return ((-1 * noteNumber * lineOffset) + (-1 * (octaveNumber - 4) * (lineOffset * 7))) + 10 * lineOffset; //If errors in octaves check this latter
    }

    Map<Character, Integer> noteNameToInt = Map.of(
        'C', 0,
        'D', 1,
        'E', 2,
        'F', 3,
        'G', 4,
        'A', 5,
        'B', 6
    );

    /**
     * Handles the import MusicXML button click.
     * Opens a file chooser to select a MusicXML file and converts it to a Song.
     */
    @FXML
    private void handleImportMusicXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open MusicXML File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("MusicXML Files", "*.musicxml", "*.xml"));
        
        File selectedFile = fileChooser.showOpenDialog(importMusicXMLBtn.getScene().getWindow());
        
        if (selectedFile != null) {
            // Get the current logged-in username to use as composer if none in file
            MusicAppFacade facade = MusicAppFacade.getInstance();
            String username = facade.getUser() != null 
                    ? facade.getUser().getUsername() 
                    : "Unknown";
                    
            // Process the selected file
            Map<String, String> result = MusicXMLToSongJsonConverter.handleMusicXMLFileSelected(selectedFile, username);
            
            if (result != null && "true".equals(result.get("success"))) {
                // Reload songs from JSON
                try {
                    facade.loadSongs();
                    
                    // Get the song ID from the result
                    String songId = result.get("id");
                    if (songId != null) {
                        // Find the imported song in the facade's song list
                        Song importedSong = null;
                        for (Song song : facade.getSongList().getSongs()) {
                            if (song.getId().toString().equals(songId)) {
                                importedSong = song;
                                break;
                            }
                        }
                        
                        if (importedSong != null) {
                            // Set it as the current song and redraw
                            currentSong = importedSong;
                            facade.setViewedSong(importedSong);
                            redraw();
                            
                            // Show success alert
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Import Successful");
                            alert.setHeaderText(null);
                            alert.setContentText("MusicXML file \"" + result.get("title") + "\" was successfully imported and is now displayed.");
                            alert.showAndWait();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading songs after import: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Import Failed");
                alert.setHeaderText(null);
                alert.setContentText(result != null ? result.get("message") : "Failed to import MusicXML file.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Creates a new empty song, using the logged-in user as the composer if available
     */
    private Song createNewEmptySong(MusicAppFacade facade) {
        try {
            // Get current user if logged in
            User currentUser = facade.getUser();
            String composer = currentUser != null ? currentUser.getUsername() : "Anonymous";
            
            // Create a basic song with proper constructor
            Song emptySong = new Song("New Song", composer);
            
            // Set the publisher to the current user if logged in
            if (currentUser != null) {
                emptySong.setPublisher(currentUser);
            }
            
            // Create an empty list of music elements for the measure
            List<MusicElement> emptyElements = new ArrayList<>();
            
            // Create a measure with default 4/4 time signature
            Measure measure = new Measure(0, 4, 4, emptyElements);
            
            // Create a list to hold the measure
            List<Measure> measures = new ArrayList<>();
            measures.add(measure);
            
            // Create a staff with treble clef
            Staff staff = new Staff("treble", measures);
            
            // Create a list to hold the staff
            List<Staff> staves = new ArrayList<>();
            staves.add(staff);
            
            // Create sheet music with piano instrument
            List<String> clefTypes = new ArrayList<>();
            clefTypes.add("treble");
            Instrument piano = new Instrument(clefTypes, "Piano");
            SheetMusic sheet = new SheetMusic(piano, staves);
            
            // Add sheet music to song
            emptySong.addSheetMusic(sheet);
            
            return emptySong;
        } catch (Exception e) {
            System.err.println("Error creating empty song: " + e.getMessage());
            e.printStackTrace();
            
            // Create a simple song with minimal structure
            Song minimalSong = new Song("Blank Song", "Anonymous");
            
            try {
                // Create minimal required elements to avoid NPEs
                List<MusicElement> emptyElements = new ArrayList<>();
                Measure measure = new Measure(0, 4, 4, emptyElements);
                List<Measure> measures = new ArrayList<>();
                measures.add(measure);
                Staff staff = new Staff("treble", measures);
                List<Staff> staves = new ArrayList<>();
                staves.add(staff);
                List<String> clefTypes = new ArrayList<>();
                clefTypes.add("treble");
                Instrument piano = new Instrument(clefTypes, "Piano");
                SheetMusic sheet = new SheetMusic(piano, staves);
                minimalSong.addSheetMusic(sheet);
            } catch (Exception ex) {
                // Log error but don't rethrow to avoid crashing
                System.err.println("Error creating minimal song structure: " + ex.getMessage());
            }
            
            return minimalSong;
        }
    }
}
