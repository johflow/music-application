package com.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading files.
 */
public class FileReaderUtil {

  private static final Logger logger = Logger.getLogger(FileReaderUtil.class.getName());
  private static final String DEFAULT_SONGS_CONTENT = "{\"songs\":[]}";
  private static final String DEFAULT_USERS_CONTENT = "{\"users\":[]}";

  /**
   * Reads the entire content of a file as a String.
   * If the file doesn't exist or is empty, creates the file with default content.
   *
   * @param filePath the path to the file
   * @return the content of the file as a String
   * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable
   *                     byte sequence is read
   */
  public String readFile(String filePath) throws IOException {
    logger.info("Attempting to read file: " + filePath);

    if (filePath == null || filePath.trim().isEmpty()) {
      String error = "Invalid file path: filePath is null or empty";
      logger.severe(error);
      throw new IllegalArgumentException(error);
    }

    Path path = Paths.get(filePath);
    File file = path.toFile();

    // Handle case where file doesn't exist or is empty
    if (!Files.exists(path) || Files.size(path) == 0) {
      logger.warning("File " + filePath + " doesn't exist or is empty. Creating with default content.");
      
      // Create parent directories if they don't exist
      if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      }
      
      // Create default content based on file type
      String defaultContent;
      if (filePath.endsWith("songs.json")) {
        defaultContent = DEFAULT_SONGS_CONTENT;
      } else if (filePath.endsWith("users.json")) {
        defaultContent = DEFAULT_USERS_CONTENT;
      } else {
        defaultContent = "{}"; // Default for other files
      }
      
      // Write default content to file
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(defaultContent);
      }
      
      return defaultContent;
    }

    try {
      byte[] fileBytes = Files.readAllBytes(path);
      String content = new String(fileBytes);
      
      // Check for empty JSON objects and replace with proper structures
      if (content.trim().isEmpty() || content.trim().equals("{}")) {
        String defaultContent;
        if (filePath.endsWith("songs.json")) {
          defaultContent = DEFAULT_SONGS_CONTENT;
        } else if (filePath.endsWith("users.json")) {
          defaultContent = DEFAULT_USERS_CONTENT;
        } else {
          defaultContent = "{}"; // Default for other files
        }
        
        // Write proper content to file
        try (FileWriter writer = new FileWriter(file)) {
          writer.write(defaultContent);
        }
        
        return defaultContent;
      }
      
      logger.info("Successfully read file: " + filePath);
      return content;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error reading the file at: " + filePath, e);
      throw e;
    }
  }
}
