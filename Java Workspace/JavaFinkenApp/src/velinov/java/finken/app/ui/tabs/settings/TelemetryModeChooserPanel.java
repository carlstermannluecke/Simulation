package velinov.java.finken.app.ui.tabs.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.xml.sax.SAXException;

import velinov.java.finken.app.filesaver.FilePathSaver;
import velinov.java.finken.app.filesaver.FilePathSaverUtils;
import velinov.java.finken.app.ui.AbsFileChooserPanel;
import velinov.java.finken.telemetry.AuxiliaryModeXmlReader;


/**
 * represents a panel for choosing the telemetry mode by a combo box.
 * 
 * @author Vladimir Velinov
 * @since Oct 19, 2015
 *
 */
public class TelemetryModeChooserPanel implements PropertyChangeListener {
  
  @SuppressWarnings("nls")
  private static final String TITLE = "Telemetry mode:";
  
  private final JPanel    mainPanel;
  private final JPanel    leftPanel;
  private final JPanel    rightPanel;
  private final JLabel    label;
  private final JComboBox combo;
  
  
  /**
   * default constructor.
   */
  @SuppressWarnings("nls")
  public TelemetryModeChooserPanel() {
    this.mainPanel  = new JPanel();
    this.leftPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
    this.rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
    this.label      = new JLabel("Telemetry mode:");
    this.combo      = new JComboBox();
    
    this.mainPanel.setLayout(new BorderLayout());
    this.mainPanel.setPreferredSize(new Dimension(1, 50));
    this.mainPanel.setBorder(BorderFactory.createTitledBorder(TITLE));
    
    this.combo.setPreferredSize(new Dimension(120, 25)); 
    this.leftPanel.add(this.label);
    this.rightPanel.add(this.combo);
    
    this.mainPanel.add(this.leftPanel, BorderLayout.WEST);
    this.mainPanel.add(this.rightPanel, BorderLayout.EAST);
    
    this._initCombo();
  }
  
  @SuppressWarnings("nls")
  private void _initCombo() {
    AuxiliaryModeXmlReader xmlReader;
    FilePathSaverUtils     fileSaverUtils;
    FilePathSaver          fileSaver;
    File                   file;
    String                 path;
    List<String>           modes;
    
    fileSaverUtils = FilePathSaverUtils.getInstance();
    fileSaver      = fileSaverUtils.getFilePathSaver(
        FilePathSaver.TELEMETRY_PATH);
    
    if (fileSaver == null) {
      throw new NullPointerException("file saver not created yet");
    }
    
    path = fileSaver.getFilePath();
    
    if (path == null) {
      return;
    }
    
    file = new File(fileSaver.getFilePath());
    
    try {
      xmlReader = new AuxiliaryModeXmlReader(file);
      xmlReader.parseXmlDocument();
    }
    catch (FileNotFoundException _e) {
      this.label.setText("error - telemetry file not found!");
      return;
    }
    catch (SAXException _e) {
      this.label.setText("Error while parsing xml file");
      return;
    }
    catch (IOException _e) {
      this.label.setText("Error opening xml file");
      return;
    }
    
    modes = xmlReader.getTelemetryModes();
    
    this.combo.removeAllItems();
    
    for (String mode : modes) {
      this.combo.addItem(mode);
    }
  }
  
  /**
   * @return the <code>JPanel</code> defining the view.
   */
  public JPanel getPanel() {
    return this.mainPanel;
  }

  @Override
  public void propertyChange(PropertyChangeEvent _evt) {
    String propName;
    
    propName = _evt.getPropertyName();
    
    if (propName.equals(AbsFileChooserPanel.FILE_CHOSEN_PROPERTY)) {
      this._initCombo();
    }
  }

}