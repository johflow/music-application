package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.logging.Logger;

public class CreateSongController extends BaseController {
    private static final Logger logger = Logger.getLogger(CreateSongController.class.getName());
    @FXML private Label songLabel;
    
    @FXML
    public void initialize() {
        super.initialize();
        // Basic initialization - team will add functionality
    }
} 