package com.frontend.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SheetDemo extends Application {

  @Override
  public void start(Stage stage) {
    // Canvas size: width x height
    Canvas canvas = new Canvas(800, 200);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    // Draw a single staff starting at x=50, y=50
    drawStaff(gc, 50, 50, 700);

    // Hard‑coded “notes” on that staff:
    // Here we’re just placing notes on successive lines/spaces:
    drawQuarterNote(gc, 100, 90);  // bottom line
    drawQuarterNote(gc, 200, 85);  // space
    drawQuarterNote(gc, 300, 80);  // 2nd line
    drawQuarterNote(gc, 400, 75);  // space
    drawQuarterNote(gc, 500, 70);  // middle line

    stage.setScene(new Scene(new StackPane(canvas)));
    stage.setTitle("Hard‑Coded Sheet Music Demo");
    stage.show();
  }

  // Draw 5 horizontal staff lines
  private void drawStaff(GraphicsContext gc, double startX, double startY, double width) {
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(1);
    for (int i = 0; i < 5; i++) {
      double y = startY + i * 10;
      gc.strokeLine(startX, y, startX + width, y);
    }
  }

  // Draw a simple quarter‑note: filled oval + stem
  private void drawQuarterNote(GraphicsContext gc, double x, double y) {
    gc.setFill(Color.BLACK);
    gc.fillOval(x, y - 5, 10, 10);              // note head
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(2);
    gc.strokeLine(x + 10, y, x + 10, y - 30);    // stem going up
  }

  public static void main(String[] args) {
    launch(args);
  }
}
