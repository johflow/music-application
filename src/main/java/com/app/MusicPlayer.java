package com.app;

import java.util.ArrayList;

import com.model.User;
import com.music.Music;
import com.service.DataAssembler;

public class MusicPlayer {

  public void playSong() {
    try {
      playLine();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void playLine() {
    Music.playNote("60*1.5");
    Music.playNote("");
    Music.playNote("D");
    Music.playNote("G");
    Music.playNote("Bb");
    Music.playNote("D");
    Music.playNote("G");
    Music.playNote("D");
    Music.playNote("Bb");
    Music.playNote("G");
    Music.playNote("C");
    Music.playNote("Db");
    Music.playNote("F");
    Music.playNote("D");
    Music.playNote("C");
    Music.playNote("F");
  }

  public static void main(String[] args) {
    MusicPlayer player = new MusicPlayer();
    player.playSong();
    ArrayList<User> users = DataAssembler.getUsers();
    System.out.println(users);
  }
}
