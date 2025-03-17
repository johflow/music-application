package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Song {
    private UUID id;
    private String title;
    private String composer;
    private User publisher;
    private List<SheetMusic> sheetMusic;
    private int pickUp;

  public Song(UUID id, String title, String composer, User publisher, List<SheetMusic> sheetMusic,
      int pickUp) {
    this.id = id;
    this.title = title;
    this.composer = composer;
    this.publisher = publisher;
    this.sheetMusic = sheetMusic;
    this.pickUp = pickUp;
  }

  public Song(String title, String composer) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.composer = composer;
        this.sheetMusic = new ArrayList<>();
        this.pickUp = 0;
    }

  public Song(UUID id, String title, String composer, int pickUp, List<SheetMusic> sheetMusic) {
    this.id = id;
    this.title = title;
    this.composer = composer;
    this.sheetMusic = sheetMusic;
    this.pickUp = pickUp;
  }

  public void addSheetMusic(Instrument instrument) {
       
    }

    public void save() {

    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getComposer() {
        return composer;
    }

    public List<SheetMusic> getSheetMusics() {
        return sheetMusic;
    }

    public int getPickUp() {
        return pickUp;
    }

    public void setPickUp(int pickUp) {
        this.pickUp = pickUp;
    }

  public void setPublisher(User publisher) {
    this.publisher = publisher;
  }
  public String toString() {
      return this.title;
  }
}

