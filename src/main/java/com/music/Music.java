package com.music;

import org.jfugue.player.Player;

public class Music {
  public static void playNote(String note) {
    Player player = new Player();
    player.play(note);
  }

  public static void playNote(int pitch, double duration) {
    Player player = new Player();
    String note = pitch + "*" + duration;
    player.play(note);
  }
}
