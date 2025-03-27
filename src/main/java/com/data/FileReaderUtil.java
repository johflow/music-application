package com.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility class for reading files.
 */
public class FileReaderUtil {

  /**
   * Reads the entire content of a file as a String.
   *
   * @param filePath the path to the file
   * @return the content of the file as a String
   * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
   */
  public String readFile(String filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }
}
