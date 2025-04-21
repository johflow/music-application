package com.frontend.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.model.ThemeColor;

import javafx.scene.Scene;

/**
 * Style manager for managing application themes and CSS file loading.
 * 
 * This singleton class is responsible for maintaining a consistent UI appearance
 * throughout the application by handling theme switching and stylesheet application.
 * It manages layered CSS loading in the following order:
 * 
 * 1. Base CSS (layout, typography, spacing)
 * 2. Theme CSS (dark or light color schemes)
 * 3. Component-specific CSS (specialized styling for specific components)
 * 
 * The class preloads all stylesheets when instantiated to improve performance
 * when applying styles to new scenes or when switching themes.
 */
public class BaseStyleManager {
    private static final Logger logger = Logger.getLogger(BaseStyleManager.class.getName());
    private static BaseStyleManager instance;
    private ThemeColor currentTheme = ThemeColor.getDefault();
    
    // Store all loaded stylesheets
    private final Map<String, String> loadedStylesheets = new HashMap<>();
    
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the style manager and preloads all stylesheets.
     */
    private BaseStyleManager() {
        preloadStylesheets();
    }
    
    /**
     * Gets the singleton instance of the BaseStyleManager.
     * 
     * If the instance doesn't exist yet, it will be created.
     * 
     * @return The singleton instance of the BaseStyleManager
     */
    public static BaseStyleManager getInstance() {
        if (instance == null) {
            instance = new BaseStyleManager();
        }
        return instance;
    }
    
    /**
     * Preloads all stylesheets into memory for fast access.
     * 
     * This method loads the base CSS, theme CSS files (dark and light),
     * and all component-specific CSS files defined in ViewConstants.
     * Each stylesheet is loaded once and cached for future use.
     */
    private void preloadStylesheets() {
        loadStylesheet(ViewConstants.BASE_CSS);
        loadStylesheet(ViewConstants.DARK_THEME_CSS);
        loadStylesheet(ViewConstants.LIGHT_THEME_CSS);
        
        for (String componentCss : ViewConstants.COMPONENT_CSS_FILES) {
            loadStylesheet(componentCss);
        }
    }
    
    /**
     * Loads a single stylesheet and stores its external form in the cache.
     * 
     * This method converts the resource path to an external form URL that
     * JavaFX can use when applying stylesheets to scenes.
     * 
     * @param path The resource path to the CSS file
     */
    private void loadStylesheet(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                loadedStylesheets.put(path, resource.toExternalForm());
            } else {
                logger.warning("Stylesheet not found: " + path);
            }
        } catch (Exception e) {
            logger.warning("Error loading stylesheet " + path + ": " + e.getMessage());
        }
    }
    
    /**
     * Sets the current application theme.
     * 
     * This method updates the current theme but does not apply it to any scenes.
     * To apply the theme to a scene, call applyTheme(Scene) after setting the theme.
     * 
     * @param themeColor The theme color to set. If null, the default theme will be used.
     */
    public void setCurrentTheme(ThemeColor themeColor) {
        this.currentTheme = themeColor != null ? themeColor : ThemeColor.getDefault();
    }
    
    /**
     * Gets the current application theme.
     * 
     * @return The current ThemeColor (DARK or LIGHT)
     */
    public ThemeColor getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Applies the current theme to a scene.
     * 
     * This method applies CSS styling to the given scene in the following layers:
     * 1. Base CSS - Common styling for layout, typography, etc.
     * 2. Theme CSS - Either dark or light theme based on current setting
     * 3. Component CSS - Specialized styling for specific components
     * 
     * The method also applies the appropriate theme class to the scene root
     * for additional styling via CSS selectors.
     * 
     * @param scene The JavaFX scene to apply theming to
     * @return True if styling was successful, false if there was an error or the scene was null
     */
    public boolean applyTheme(Scene scene) {
        if (scene == null) return false;
        
        try {
            // Clear existing stylesheets
            scene.getStylesheets().clear();
            
            // Always add base CSS first
            addStylesheetToScene(scene, ViewConstants.BASE_CSS);
            
            // Add the theme-specific CSS
            String themeCss = currentTheme == ThemeColor.DARK ? ViewConstants.DARK_THEME_CSS : ViewConstants.LIGHT_THEME_CSS;
            addStylesheetToScene(scene, themeCss);
            
            // Add component-specific CSS files
            for (String componentCss : ViewConstants.COMPONENT_CSS_FILES) {
                addStylesheetToScene(scene, componentCss);
            }
            
            // Add theme class to scene root
            if (scene.getRoot() != null) {
                scene.getRoot().getStyleClass().removeAll("light-theme", "dark-theme");
                scene.getRoot().getStyleClass().add(
                    currentTheme == ThemeColor.DARK ? "dark-theme" : "light-theme"
                );
            }
            
            return true;
        } catch (Exception e) {
            logger.warning("Error applying theme to scene: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Adds a stylesheet to a scene if it has been preloaded.
     * 
     * This method gets the external form of the stylesheet from the cache
     * and adds it to the scene if it exists and hasn't been added already.
     * 
     * @param scene The scene to add the stylesheet to
     * @param stylesheetPath The resource path of the stylesheet to add
     */
    private void addStylesheetToScene(Scene scene, String stylesheetPath) {
        String externalForm = loadedStylesheets.get(stylesheetPath);
        if (externalForm != null) {
            if (!scene.getStylesheets().contains(externalForm)) {
                scene.getStylesheets().add(externalForm);
            }
        } else {
            logger.warning("Stylesheet not loaded: " + stylesheetPath);
        }
    }
    
    /**
     * Checks if the current theme is dark.
     * 
     * A convenience method to determine whether the dark theme is currently active.
     * 
     * @return true if the current theme is dark, false if it's light
     */
    public boolean isDark() {
        return currentTheme == ThemeColor.DARK;
    }
} 