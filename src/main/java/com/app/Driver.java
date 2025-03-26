package com.app;

import com.model.Staff;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.model.Instrument;
import com.model.Measure;
import com.model.MusicAppFacade;
import com.model.MusicElement;
import com.model.Note;
import com.model.SheetMusic;
import com.model.Song;

public class Driver {

  public static void main(String[] args) {
    MusicAppFacade facade = MusicAppFacade.getInstance();
    // Fred trying to register as Fellicia
    //facade.register("ffredrickson", "mypassword", "fmail@gmail.com");
    // Fred making his own unique account
    facade.register("ffred", "mypassword", "fmail@gmail.com");
    facade.logout();

    // Fred logging in
    facade.login("ffred", "mypassword");

    // Fred searching for Tom Petty songs
    List<Song> songs = facade.searchForSongs("Tom Petty");
    System.out.println(songs);
    
    // Fred selecting Free Fallin', playing it, printing it, and logging out
    facade.setViewedSong(songs.get(2));
    facade.playViewedSong();
    facade.printViewedSong();
    facade.logout();

    // Fellicia logging in
    facade.login("ffredrickson", "password123");

    // Fellicia creating a new song
    facade.createSong("A horses journey", "Fellicia");

    // Fellicia opens their created song
    facade.setViewedSong(facade.searchForSong("A horses journey"));

    // Fellicia adds necessary prereqs for adding 2 measures with notes
    facade.addSheetMusic(new SheetMusic(new Instrument(Arrays.asList("Treble"), "Voice")));

    facade.addStaff(new Staff("Treble", new ArrayList<Measure>()));

    // Fellicia uses the UI to add notes to her 1st measure
    List<MusicElement> notes1 = new ArrayList<>();
    notes1.add(new Note("note", 60.0, 60, "C4", 1.0, 'q', 0, false, ""));
    notes1.add(new Note("note", 62.0, 62, "D4", 1.0, 'q', 0, false, ""));
    notes1.add(new Note("note", 64.0, 64, "E4", 1.0, 'q', 0, false, ""));
    notes1.add(new Note("note", 65.0, 65, "F4", 1.0, 'q', 0, false, ""));
    
    facade.addMeasure(new Measure(0, 4, 4, 120, notes1));

    // Fellicia uses the UI to add notes to her 2nd measure
    List<MusicElement> notes2 = new ArrayList<>();
    notes2.add(new Note("note", 60.0, 60, "E4", 1.0, 'q', 0, false, ""));
    notes2.add(new Note("note", 62.0, 62, "D4", 1.0, 'q', 0, false, ""));
    notes2.add(new Note("note", 64.0, 64, "C4", 1.0, 'q', 0, false, ""));
    notes2.add(new Note("note", 65.0, 65, "B4", 1.0, 'q', 0, false, ""));
    
    facade.addMeasure(new Measure(0, 4, 4, 120, notes2));

    // Fellicia plays the song and logs out
    facade.playViewedSong();
    facade.logout();
    
    // Fred logs in, searches for Fellicias new song and then plays it
    facade.login("ffred", "mypassword");
    facade.setViewedSong(facade.searchForSong("A horses journey"));
    facade.playViewedSong();



  }
}
