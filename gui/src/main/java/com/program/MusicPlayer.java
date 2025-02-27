package com.program;

import com.music.*;
import java.lang.Thread;

public class MusicPlayer {

  public void playSong() {
    try {
      playLine();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void playLine() {
    Music.playNote("G");
    Music.playNote("Bb");
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
  }
}
