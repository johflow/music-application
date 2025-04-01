package com.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading files.
 */
public class FileReaderUtil {

  private static final Logger logger = Logger.getLogger(FileReaderUtil.class.getName());

  /**
   * Reads the entire content of a file as a String.
   *
   * @param filePath the path to the file
   * @return the content of the file as a String
   * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable
   *                     byte sequence is read
   */
  public String readFile(String filePath) throws IOException {

    if (filePath == null || filePath.trim().isEmpty()) {
      logger.severe("Invalid file path: filePath is null or empty");
      return "";
    }

    Path path = Paths.get(filePath);

    if (!Files.exists(path)) {
      logger.severe("File not found at: " + filePath);
      return "";
    }

    try {
      byte[] fileBytes = Files.readAllBytes(path);
      return new String(fileBytes);

    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error reading the file at: " + filePath, e);
      return "";
    }
  }
}
