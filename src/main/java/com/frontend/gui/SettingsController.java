package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.logging.Logger;

public class SettingsController extends BaseController {
    private static final Logger logger = Logger.getLogger(SettingsController.class.getName());
    @FXML private Label settingsLabel;
    
    @FXML
    public void initialize() {
        super.initialize();
        // Basic initialization - team will add functionality
    }
} 