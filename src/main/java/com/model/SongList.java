package com.model;

import java.sql.Array;
import java.util.ArrayList;
import java.util.UUID;

public class SongList {

  private ArrayList<Song> songs;

  private SongList() {
    //TODO
  }

  public static SongList getInstance() {
    //TODO
    return new SongList();
  }

  public void addSong(String title, String composer, Instrument instrument, int tempo,
      KeySignature key, int timeSignatureNumerator, int timeSignatureDenominator,
      int numberOfMeasures, int pickup) {
    //TODO
  }

  public void removeSong(UUID songID) {
    //TODO
  }

  public ArrayList<Song> searchSong(String keyword) {
    //TODO
    return new ArrayList<Song>();
  }

  public void sortSheetMusic(String criterion) {
    //TODO
  }

  public void save() {
    //TODO
  }


}
