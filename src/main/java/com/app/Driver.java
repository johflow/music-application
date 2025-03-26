package com.app;

import com.model.MusicAppFacade;

public class Driver {

  public static void main(String[] args) {
    MusicAppFacade musicAppFacade = MusicAppFacade.getInstance();
    musicAppFacade.register("ffredrickson", "mypassword", "fmail@gmail.com");
  }

}
