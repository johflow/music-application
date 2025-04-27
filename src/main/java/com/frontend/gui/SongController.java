package com.frontend.gui;

import com.model.Chord;
import com.model.DurationElement;
import com.model.Measure;
import com.model.MusicElement;
import com.model.Note;
import com.model.Song;
import com.model.MusicAppFacade;
import com.model.Tuplet;
import com.service.PlaybackTask;
import com.service.SongPlayer;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SongController {

    @FXML private HBox        hudBar;
    @FXML private Button      exitSongBtn;
    @FXML private Button      playSongBtn;
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
        // 1) Prepare the canvas & put it in the scrollPane
        canvas = new Canvas();
        scrollPane.setContent(canvas);
        scrollPane.setPannable(true);
        gc = canvas.getGraphicsContext2D();

        // 2) Load your song & figure out page count
        MusicAppFacade facade = MusicAppFacade.getInstance();
        facade.loadSongs();
        facade.setViewedSong(facade.positionToLoadedSong(0));
        currentSong = facade.getViewedSong();

        // 3) Wire up HUD buttons
        exitSongBtn.setOnAction(e -> { handleStop(); });
        playSongBtn.setOnAction(e -> { handlePlay(); });

        // 4) When the ScrollPane viewport appears, size & draw
        scrollPane.viewportBoundsProperty().addListener((obs,oldB,newB) -> {
            canvas.setWidth(newB.getWidth());
            canvas.setHeight(newB.getHeight());
            redraw();
        });

        // 5) After everything lays out, do the first draw
        Platform.runLater(() -> {
            Bounds vp = scrollPane.getViewportBounds();
            canvas.setWidth(vp.getWidth());
            canvas.setHeight(vp.getHeight());
            redraw();
        });
    }

    private void exitSong() {
        //TODO
    }

    private void redraw() {

        int measures    = currentSong.getSheetMusic().get(0)
            .getStaves().get(0)
            .getMeasures().size();
        int perLine     = 4;                       // whatever you want
        int lines       = (int) Math.ceil(measures / (double) perLine);

        measureLength   = canvas.getWidth() / 6;   // keep your ratio
        measureHeight   = canvas.getHeight() / 12;

        double neededH  = measureHeight * 4 * lines + 2 * (height / 10);

        canvas.setHeight(neededH);
        width = canvas.getWidth();
        height = canvas.getHeight();

        System.out.println("Redrawing canvas. Width: " + width + ", Height: " + height); // <-- Add this
        gc.clearRect(0,0,width,height);

        // draw grid or sheet music only for currentPage…
        drawSong(currentSong, gc, width, neededH);
    }

    private void drawSong(Song song, GraphicsContext gc, double width, double height) {
        double x = width/10, y = 100;

        for (Measure measure : currentSong.getSheetMusic().get(0).getStaves().get(0).getMeasures()) {
            drawMeasure(x, y, measure);
            x += measureLength + 1;
            if (x > width - measureLength) {
                x = width/10;
                y += 6 * measureHeight + 1;
            }
        }

        y = 100 + measureHeight*2;
        x = width/10;
        for (Measure measure : currentSong.getSheetMusic().get(0).getStaves().get(3).getMeasures()) {
            drawMeasure(x, y, measure);
            x += measureLength + 1;
            if (x > width - measureLength) {
                x = width/10;
                y += 6 * measureHeight + 1;
            }
        }
    }

    private void drawMeasure(double x, double y, Measure measure) {

        int numOfLines = 5;
        gc.setStroke(Color.BLACK);
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
}
