package com.app;

import com.model.Instrument;
import com.service.DataAssembler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.json.simple.parser.ParseException;

import com.model.Song;
import com.model.ThemeColor;
import com.model.User;
import com.service.DataWriter;

public class MusicPlayer {

  public static void main(String[] args) throws IOException, ParseException {
//// Josh's tests for DataWriter
//	// Create test data
//	List<User> users = new ArrayList<>();
//	List<Song> songs = new ArrayList<>();
//
//
//	// Create a test user
//	User testUser1 = new User(null, "testuser", "password123");
//	testUser1.setThemeColor(ThemeColor.BLUE);
//	testUser1.setEmail("testuser@example.com");
//
//	User testUser2 = new User("randomemail@email.com", "testuser", "password123"); // this makes a new user
//	testUser2.setThemeColor(ThemeColor.DEEP_PURPLE); // works as intended just need to somehow implement that in front end
//
//	testUser1.followUser(testUser2); // works as intended
//	testUser2.followUser(testUser1); // also works as intended
//	testUser1.followUser(testUser1); //doesnt add user to user cus thats dumb so works as intended
//
//	// Create a test song
//	Song testSong = new Song("Test Song", "Test Composer", testUser1);
//	testSong.setComposer("Test Composer");
//
//	testUser1.addFavoriteSong(testSong);
//	testUser1.addFavoriteSong(testSong);
//
//	testSong.setPickUp(0);
//	songs.add(testSong);
//	users.add(testUser1);
//	users.add(testUser2);
//
//
//	// Save test data using DataWriter
//	try {
//		boolean usersSaved = DataWriter.saveUsers(users);
//		boolean songsSaved = DataWriter.saveSongs(songs);
//
//		if (usersSaved && songsSaved) {
//			System.out.println("Test data saved successfully!");
//		} else {
//			System.out.println("Error saving test data");
//		}
//	} catch (Exception e) {
//		System.err.println("Error during test:");
//		System.err.println(e.getMessage());
//		e.printStackTrace();
//	}
//
//		DataAssembler dataAssembler = new DataAssembler();
//	System.out.println(dataAssembler.getAssembledSongs().getFirst());

         Player player = new Player();

     DataAssembler assembler = new DataAssembler();
 //    System.out.println(assembler.getAssembledUsers(DataConstants.USER_FILE_LOCATION, DataConstants.SONG_FILE_LOCATION));
 //    System.out.println(assembler.getAssembledSongs(DataConstants.SONG_FILE_LOCATION, DataConstants.USER_FILE_LOCATION));

     // Right-hand melody (Voice 0)
     Pattern pattern = new Pattern("A5 A5 E6 E6 A6 A6 E6 E6");
     Pattern rightHand = new Pattern("V0 I[Piano] Rq").add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern);

     // Left-hand chords (Voice 1)
     Pattern leftHand = new Pattern("V1 I[Piano] Rq Rwww Rh G4h F3+C4+A4 E5 A4 G4 A4w G3h. G4 A4 E5 A4 G4 A3+C4+E4+A4 E5 A4 G4 A4h. E4 G4h. D4 G3+E4 G4 A4 B4 F3+A4 E5 A4 G4 A4w G3h. G4 A4 E5 B4 C5 C4+E4+B4 C5 B4 A4 E4h. E3 F2h. D4 G3+E4 G4 A4 B4 F3+A4 E5 A4 G4 A4h C4h G3h.+B3h. G4 A4 E5 A4 G4 C4+E4+A4 E5 A4 G4 A4h. E4 G4h. D4 G3+E4 G4 A4 B4 F3+C4+A4 E5 A4 G4 A4h D4 C4 G3h.+B4h. G4 A4 E5 B4 C5 C4+E4+B4 C5 D5 E5 A4h E4h F3h C3h");

     // Merge both patterns into a single pattern and play
     Pattern fullSong = new Pattern().add(rightHand).add(leftHand);
     Pattern tuplet = new Pattern("V0 R R R R C*14:1 C*5:4 C*5:4 C*5:4 C*5:4 R R R R Cw");
     Pattern beat = new Pattern("V1 C C C C C C C C R R R R Cw");
     Pattern comparison = new Pattern().add(tuplet).add(beat);
     //player.play("R C C C C Cq*3:2 Cq*3:2 Cq*3:9 C C");
     player.play("V0 C V1 E V3 G");


  }
}
