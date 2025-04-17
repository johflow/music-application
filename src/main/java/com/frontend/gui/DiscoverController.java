package com.frontend.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.logging.Logger;

public class DiscoverController extends BaseController {
    private static final Logger logger = Logger.getLogger(DiscoverController.class.getName());
    @FXML private Label discoverLabel;
    
    @FXML
    public void initialize() {
        super.initialize();
        // Basic initialization - team will add functionality
    }
} 