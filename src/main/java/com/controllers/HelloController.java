package com.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    private void onHelloButtonClick(ActionEvent event) {
        welcomeText.setText("Hello from the controller!");
    }
}


    

