package com.app;

import com.model.MusicAppFacade;
import com.model.Song;
import java.util.List;


public class Driver {

  public static void main(String[] args) {
    MusicAppFacade facade = MusicAppFacade.getInstance();
    facade.register("ffredrickson", "mypassword", "fmail@gmail.com");

    facade.register("ffred", "mypassword", "fmail@gmail.com");
    facade.logout();

    facade.login("ffred", "mypassword");

    List<Song> songs = facade.searchForSongs("Tom Petty");
    System.out.println(songs);
    
    facade.setViewedSong(songs.get(2));
    facade.playViewedSong();
    facade.printSong();
    facade.logout();
    
    facade.login("ffredrickson", "password123");
  }



}
