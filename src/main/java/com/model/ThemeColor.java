package com.model;

/**
 * Represents theme colors that users can select for their profile
 */
public enum ThemeColor {
    LIGHT("#F5F5F5", "Light"),
    DARK("#212121", "Dark");
    
    private final String hexCode;
    private final String colorName;
            
    /**
     * Constructor for ThemeColor
     * 
     * @param hexCode The hex color code
     * @param colorName The display name of the color
     */
    ThemeColor(String hexCode, String colorName) {
        this.hexCode = hexCode;
        this.colorName = colorName;
    }
    
    /**
     * Gets the hex color code
     * 
     * @return The hex color code
     */
    public String getHexCode() {
        return hexCode;
    }
    
    /**
     * Gets the display name of the color
     * 
     * @return The display name
     */
    public String getColorName() {
        return colorName;
    }
    
    /**
     * Gets the default theme color
     * 
     * @return The default theme color (DARK)
     */
    public static ThemeColor getDefault() {
        return DARK;
    }
    
    /**
     * Gets a theme color by its display name
     * 
     * @param name The display name to search for
     * @return The matching theme color, or the default if not found
     */
    public static ThemeColor getByName(String name) {
        if (name == null || name.isEmpty()) {
            return getDefault();
        }
        
        for (ThemeColor color : values()) {
            if (color.getColorName().equalsIgnoreCase(name)) {
                return color;
            }
        }
        
        return getDefault();
    }
    
    @Override
    public String toString() {
        return colorName;
    }
} 