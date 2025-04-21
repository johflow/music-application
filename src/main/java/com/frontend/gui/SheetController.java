package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class SheetController implements Initializable {
  @FXML private Canvas canvas;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    drawStaff(gc, 50, 50, 700);
    // hard‑coded quarter notes at different staff positions:
    drawQuarterNote(gc, 100, 90);
    drawQuarterNote(gc, 200, 85);
    drawQuarterNote(gc, 300, 80);
    drawQuarterNote(gc, 400, 75);
    drawQuarterNote(gc, 500, 70);
  }

  private void drawStaff(GraphicsContext gc, double startX, double startY, double width) {
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(1);
    for (int i = 0; i < 5; i++) {
      double y = startY + i * 10;
      gc.strokeLine(startX, y, startX + width, y);
    }
  }

  private void drawQuarterNote(GraphicsContext gc, double x, double y) {
    gc.setFill(Color.BLACK);
    gc.fillOval(x, y - 5, 10, 10);            // note head
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);
    gc.strokeLine(x + 10, y, x + 10, y - 30);  // stem
  }
}
