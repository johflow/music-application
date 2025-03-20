package com.app;

import com.data.FileReaderUtil;
import com.data.SongJsonParser;
import com.data.UserJsonParser;
import com.model.DataConstants;
import com.model.ParsedUser;
import com.model.User;
import com.service.DataAssembler;
import java.io.IOException;
import java.util.List;
import org.jfugue.player.Player;
import org.jfugue.pattern.Pattern;
import org.json.simple.parser.ParseException;

public class MusicPlayer {

  public static void main(String[] args) throws IOException, ParseException {
    Player player = new Player();

    DataAssembler assembler = new DataAssembler();
//    System.out.println(assembler.getAssembledUsers(DataConstants.USER_FILE_LOCATION, DataConstants.SONG_FILE_LOCATION));
//    System.out.println(assembler.getAssembledSongs(DataConstants.SONG_FILE_LOCATION, DataConstants.USER_FILE_LOCATION));

    // Right-hand melody (Voice 0)
    Pattern pattern = new Pattern("A5 A5 E6 E6 A6 A6 E6 E6");
    //Pattern rightHand = new Pattern("V0 I[Piano] Rq").add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern).add(pattern);

    // Left-hand chords (Voice 1)
    //Pattern leftHand = new Pattern("V1 I[Piano] Rq Rwww Rh G4h F3+C4+A4 E5 A4 G4 A4w G3h. G4 A4 E5 A4 G4 A3+C4+E4+A4 E5 A4 G4 A4h. E4 G4h. D4 G3+E4 G4 A4 B4 F3+A4 E5 A4 G4 A4w G3h. G4 A4 E5 B4 C5 C4+E4+B4 C5 B4 A4 E4h. E3 F2h. D4 G3+E4 G4 A4 B4 F3+A4 E5 A4 G4 A4h C4h G3h.+B3h. G4 A4 E5 A4 G4 C4+E4+A4 E5 A4 G4 A4h. E4 G4h. D4 G3+E4 G4 A4 B4 F3+C4+A4 E5 A4 G4 A4h D4 C4 G3h.+B4h. G4 A4 E5 B4 C5 C4+E4+B4 C5 D5 E5 A4h E4h F3h C3h");

    Pattern rightHand = new Pattern("T39 V0 A2hq+E3hq+C4hq C3+G3+E4 D3+A3+D4 D2*14:1 F#2*14:1 A2*14:1 D3*14:1 F#3*14:1 D4*14:1");
    Pattern leftHand = new Pattern("V1 D5s E5s D5s E5s D5s E5s D5s E5s D5s E5s C5s E5s B5s E5s Bbs E5i- E5-q-+A+Gb");
    // Merge both patterns into a single pattern and play
    Pattern fullSong = new Pattern().add(rightHand).add(leftHand);
    Pattern tuplet = new Pattern("V0 R R R R C*14:1 C*5:4 C*5:4 C*5:4 C*5:4 R R R R Cw");
    Pattern beat = new Pattern("V1 C C C C C C C C R R R R Cw");
    Pattern comparison = new Pattern().add(tuplet).add(beat);
    player.play("C C C Ci-*3:2 C-i-*3:2 C-i*3:2 C C C C");
    //player.play(fullSong);

  }
}
