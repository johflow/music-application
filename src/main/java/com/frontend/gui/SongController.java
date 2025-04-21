package com.frontend.gui;

import com.model.DurationElement;
import com.model.Measure;
import com.model.MusicElement;
import com.model.Note;
import com.model.Song;
import com.model.MusicAppFacade;
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
    private int measureLength = 100;
    private int measureHeight = 30;

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
        exitSongBtn.setOnAction(e -> { exitSong(); });
        playSongBtn.setOnAction(e -> { facade.playViewedSong(); });

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
        double w = canvas.getWidth(), h = canvas.getHeight();
        System.out.println("Redrawing canvas. Width: " + w + ", Height: " + h); // <-- Add this
        gc.clearRect(0,0,w,h);

        // draw grid or sheet music only for currentPage…
        drawSong(currentSong, gc, w, h);
    }

    private void drawSong(Song song, GraphicsContext gc, double width, double height) {
        int x = 30, y = 30;
        for (Measure measure : currentSong.getSheetMusic().getFirst().getStaves().getFirst().getMeasures()) {
            drawMeasure(x, y, measure);
            x += measureLength + 1;
            if (x > width - measureLength) {
                x = 30;
                y += 4 * measureHeight + 1;
            }
        }
    }

    private void drawMeasure(int x, int y, Measure measure) {

        int numOfLines = 5;
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x, y, x, y + measureHeight);
        gc.strokeLine(x + measureLength, y, x + measureLength, y + measureHeight);
        for (int i = 0; i < 5; ++i) {
            gc.strokeLine(x, y + ((double) (measureHeight * i) / 4), x + measureLength, y + ((double) (measureHeight * i) / 4) );
        }
        double duration = (double) measure.getTimeSignatureNumerator() / measure.getTimeSignatureDenominator();
        drawMusicElements(x, y, measure.getMusicElements(), duration);
    }

    private void drawMusicElements(int x, int y, List<MusicElement> musicElements, double duration) {
        for (MusicElement element : musicElements) {
            drawMusicElement(x, y, element);
            x += (int) (((DurationElement) element).getDuration() * measureLength);
            duration -= ((DurationElement) element).getDuration();
        }
    }

    private void drawMusicElement(int x, int y, MusicElement musicElement) {
        drawNote(x, y, (Note) musicElement);
    }

    private void drawNote(int x, int y, Note note) {
        y += getDurationElementYPos(note, 4);
        drawNoteHead(x, y, note);
        drawNoteStem(x, y, note);
    }

    private void drawNoteHead(int x, int y, Note note) {
        if (note.getDuration() < 0.5) {
            gc.fillOval(x, y, 9, 9);
            return;
        }
        gc.strokeOval(x, y, 9, 9);
    }

    private void drawNoteStem(int x, int y, Note note) {
        if (note.getDuration() == 1.0) {
            return;
        }
        if (note.getMidiNumber() > 69) {
            gc.strokeLine(x, y + 3, x, y + 20);
            return;
        }
        gc.strokeLine(x + 9, y + 3, x + 9, y - 20);
    }

    private double getTotalMeasureDuration(Measure measure) {
        return (double) measure.getTimeSignatureDenominator() / measure.getTimeSignatureNumerator();
    }

    private int getDurationElementYPos(DurationElement element, int lineOffset) {
        if (element.getType().equals("rest")) return 0; //TODO offset to B4
        int noteNumber = noteNameToInt.get(((Note) element).getNoteName().charAt(0));
        int octaveNumber = ((Note) element).getNoteName().charAt((((Note) element).getNoteName()).length() - 1) - '0';
        return ((-1 * noteNumber * lineOffset) + (-1 * (octaveNumber - 4) * (lineOffset * 7))) + 11 * lineOffset; //If errors in octaves check this latter
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
