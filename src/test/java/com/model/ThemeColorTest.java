package com.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThemeColorTest {

    @Test
    public void testDefaultThemeColorIsBlue() {
        // Should return BLUE as default
        assertEquals(ThemeColor.BLUE, ThemeColor.getDefault());
    }

    @Test
    public void testGetByNameValid() {
        // Should return correct color by name
        assertEquals(ThemeColor.RED, ThemeColor.getByName("Red"));
    }

    @Test
    public void testGetByNameCaseInsensitive() {
        // Should work regardless of case
        assertEquals(ThemeColor.GREEN, ThemeColor.getByName("gReEn"));
    }

    @Test
    public void testGetByNameInvalidReturnsDefault() {
        // Invalid name returns default
        assertEquals(ThemeColor.getDefault(), ThemeColor.getByName("invalid"));
    }

    @Test
    public void testGetByNameNullReturnsDefault() {
        // Null input returns default
        assertEquals(ThemeColor.getDefault(), ThemeColor.getByName(null));
    }

    @Test
    public void testHexCodeRetrieval() {
        // Should return expected hex code
        assertEquals("#E53935", ThemeColor.RED.getHexCode());
    }

    @Test
    public void testToStringReturnsColorName() {
        // Should return color name as string
        assertEquals("Blue", ThemeColor.BLUE.toString());
    }
}
