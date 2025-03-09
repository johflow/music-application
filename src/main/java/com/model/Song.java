package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class Song {
    private UUID id;
    private String title;
    private String composer;
    private ArrayList<SheetMusic> sheetMusics;
    private int pickUp;

    public Song(String title, String composer) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.composer = composer;
        this.sheetMusics = new ArrayList<>();
        this.pickUp = 0;
    }

    public Song(UUID id, String title, String composer) {
        this.id = id;
        this.title = title;
        this.composer = composer;
        this.sheetMusics = new ArrayList<>();
        this.pickUp = 0;
    }

    public void addSheetMusic(Instrument instrument) {
       
    }

    public void save() {

    }

    // Getters and setters
    public UUID getSongId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getComposer() {
        return composer;
    }

    public ArrayList<SheetMusic> getSheetMusics() {
        return sheetMusics;
    }

    public int getPickUp() {
        return pickUp;
    }

    public void setPickUp(int pickUp) {
        this.pickUp = pickUp;
    }
}

