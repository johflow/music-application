package com.model;

import java.util.ArrayList;
import java.util.UUID;
import com.service.DataWriter;

public class SongList {
    private static SongList instance;
    private ArrayList<Song> songs;

    private SongList() {
        this.songs = new ArrayList<>();
    }

    public static SongList getInstance() {
        if (instance == null) {
            instance = new SongList();
        }
        return instance;
    }

    public void addSong(String title, String composer, Instrument instrument, int tempo, int key, int timeSignatureNumerator, int timeSignatureDenominator, int numberOfMeasures, int pickup) {
        Song newSong = new Song(title, composer);
        newSong.setPickUp(pickup);
        newSong.addSheetMusic(instrument);
        songs.add(newSong);
    }

    /*public void removeSong(UUID songID) {
      songs.removeIf(song -> song.getId().equals(songID));
      }*/

    /*public Song getSong(UUID songID) {
      for (Song song : songs) {
        if (song.getId().equals(songID)) {
          return song;
        }
      }
      return null;
    }*/

    public ArrayList<Song> searchSongs(String searchQuery) {
        ArrayList<Song> results = new ArrayList<>();
        for (Song song : songs) {
            if (song.getTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                song.getComposer().toLowerCase().contains(searchQuery.toLowerCase())) {
                results.add(song);
            }
        }
        return results;
    }

    public void sortSheetMusic(String criteria) {

    }

    /*public void save() {
      DataWriter.saveSong();
    }*/

    public ArrayList<Song>getSongs() {
      return songs;
    }
}
