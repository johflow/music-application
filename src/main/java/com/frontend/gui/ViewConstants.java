package com.frontend.gui;

public class ViewConstants {
    // Base path for all FXML files
    private static final String BASE_PATH = "/com/frontend/gui/";
    
    // FXML Views
    public static final String BASE_LAYOUT_VIEW = BASE_PATH + "base-layout.fxml";
    public static final String LOGIN_VIEW = BASE_PATH + "login.fxml";
    public static final String SIGNUP_VIEW = BASE_PATH + "signup.fxml";
    public static final String DISCOVER_VIEW = BASE_PATH + "discover.fxml";
    public static final String PROFILE_VIEW = BASE_PATH + "profile.fxml";
    public static final String CREATE_SONG_VIEW = BASE_PATH + "viewSong.fxml";
    public static final String SETTINGS_VIEW = BASE_PATH + "settings.fxml";

    // CSS Files
    public static final String BASE_CSS = BASE_PATH + "base.css";
    public static final String LOGIN_CSS = BASE_PATH + "login.css";
    public static final String SIGNUP_CSS = BASE_PATH + "signup.css";
    public static final String DISCOVER_CSS = BASE_PATH + "discover.css";
    public static final String PROFILE_CSS = BASE_PATH + "profile.css";
    public static final String CREATE_SONG_CSS = BASE_PATH + "createSong.css";
    public static final String SETTINGS_CSS = BASE_PATH + "settings.css";

    // Theme CSS Files
    public static final String DARK_THEME_CSS = BASE_PATH + "dark-theme.css";
    public static final String LIGHT_THEME_CSS = BASE_PATH + "light-theme.css";

    // Component CSS Files to be loaded with every theme
    public static final String[] COMPONENT_CSS_FILES = {
        DISCOVER_CSS,
        PROFILE_CSS,
        SETTINGS_CSS
    };

    // Profile Pictures
    public static final String PROFILE_IMAGES_PATH = "/images/profiles/";
    public static final String DEFAULT_PROFILE_IMAGE = "/images/profiles/default_profile.png";
    public static final String USER_PROFILE_IMAGES_DIR = "src/main/resources/images/profiles/";
}