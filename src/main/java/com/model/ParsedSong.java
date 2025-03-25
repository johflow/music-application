package com.model;

import java.util.UUID;

public class ParsedSong {

  private final Song song;
  private final UUID publisher;

  public ParsedSong(Song song, UUID publisher) {
    this.song = song;
    this.publisher = publisher;
  }

  public Song getSong() {
    return song;
  }

  public UUID getPublisher() {
    return publisher;
  }
}