package com.service;

import javafx.concurrent.Task;
import javax.sound.midi.Sequence;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;

public class PlaybackTask extends Task<Void> {
  private final Pattern pattern;
  private final ManagedPlayer player = new ManagedPlayer(); // gives start/stop/pause controls

  public PlaybackTask(Pattern pattern) { this.pattern = pattern; }

  @Override protected Void call() throws Exception {
    // ManagedPlayer.start(*) is non-blocking; Player.play(*) is blocking –
    // we want the latter so the Task completes when the song ends.
    Sequence seq = new Player().getSequence(pattern);
    player.start(seq);          // <- runs on this background thread
    return null;
  }
  @Override protected void cancelled() { player.finish(); } // tidy up if user presses “Stop”

  public void stopPlayback() {
    player.finish();
    cancel();
  }
}

