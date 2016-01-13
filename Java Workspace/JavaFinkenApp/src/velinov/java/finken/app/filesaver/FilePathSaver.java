package velinov.java.finken.app.filesaver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import velinov.java.finken.IOUtilities;

/**
 * 
 * an utility class, that saves the default paths to xml files in a text file
 * in the default working directory.
 * 
 * @author Vladimir Velinov
 * @since Oct 11, 2015
 *
 */
public class FilePathSaver {
  
  /**
   * represents the key used to identify the <code>FilePathSaver</code> of the
   * telemetry file, stored in the <code>FilePathSaverUtils</code>.
   */
  @SuppressWarnings("nls")
  public static String TELEMETRY_PATH = "telemetryPath";
  
  /**
   * represents the key used to identify the <code>FilePathSaver</code> of the
   * aircraft file, stored in the <code>FilePathSaverUtils</code>.
   */
  @SuppressWarnings("nls")
  public static String AIRCRAFT_PATH  = "aircraftPath";
  
  /**
   * the user working directory where to save the files.
   */
  private final String directory;
  
  /**
   * the file name.
   */
  private final String fileName;
  
  /**
   * the absolute path to the file as a String.
   */
  private String       filePath;
  
  /**
   * default constructor.
   * 
   * @param _fileName
   *          the name of the file where the path will be saved.
   */
  @SuppressWarnings("nls")
  public FilePathSaver(String _fileName) {
    if (_fileName == null) {
      throw new NullPointerException("null filename");
    }
    
    this.directory = System.getProperty("user.dir");
    this.fileName  = _fileName;
    
    if (!this.loadFilePathFromFile()) {
      this.filePath = "not specified";
    }
  }
  
  /**
   * @return the saved absolute file path.
   */
  public String getFilePath() {
    return this.filePath;
  }
  
  /**
   * saves the specified <code>File</code> absolute path to the file.
   *  
   * @param _filePath the file absolute path
   * 
   * @return <code>true</code> if operation is successfull.
   */
  @SuppressWarnings("nls")
  public boolean saveFilePathToFile(File _filePath) {
    File           file;
    FileWriter     fileWriter;
    BufferedWriter bufferedWriter = null;
    
    file = new File(this.directory + "/" + this.fileName);
    
    if (!file.exists()) {
      try {
        file.createNewFile();
      }
      catch (IOException _e) {
        _e.printStackTrace();
        return false;
      }
    }
    
    try {
      fileWriter     = new FileWriter(file);
      bufferedWriter = new BufferedWriter(fileWriter);
      
      bufferedWriter.write(_filePath.getAbsolutePath());
      this.filePath  = _filePath.getAbsolutePath();
      
      return true;
    }
    catch (IOException _e) {
      _e.printStackTrace();
      return false;
    }
    finally {
      IOUtilities.closeSilently(bufferedWriter);
    }
    
  }
  
  /**
   * loads the file path from the text file.
   * 
   * @return true if successfull.
   */
  @SuppressWarnings("nls")
  public boolean loadFilePathFromFile() {
    File           file;
    FileReader     fileReader;
    BufferedReader buffReader;
    String         path;
    
    file       = new File(this.directory + "/" + this.fileName);
    buffReader = null;
    
    if (!file.exists()) {
      return false;
    }
    
    try {
      fileReader = new FileReader(file);
      buffReader = new BufferedReader(fileReader);
      
      if ((path = buffReader.readLine()) == null) {
        return false;
      }
      else {
        this.filePath = path;
      }
    }
    catch (IOException _e) {
      _e.printStackTrace();
      return false;
    }
    finally {
      IOUtilities.closeSilently(buffReader);
    }

    return true;
  }
  
 
}