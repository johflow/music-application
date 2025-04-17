package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.logging.Logger;

public class ProfileController extends BaseController {
    private static final Logger logger = Logger.getLogger(ProfileController.class.getName());
    @FXML private Label profileLabel;
    
    @FXML
    public void initialize() {
        super.initialize();
        // Basic initialization - team will add functionality
    }
} 