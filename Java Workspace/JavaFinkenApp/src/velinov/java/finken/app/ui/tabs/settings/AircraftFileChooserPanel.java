package velinov.java.finken.app.ui.tabs.settings;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import velinov.java.finken.app.filesaver.FilePathSaver;
import velinov.java.finken.app.ui.AbsFileChooserPanel;
import velinov.java.finken.app.ui.UIConstants;


/**
 * an <code>AbsFileChooserPanel</code> that chooses the aircraft file.
 * 
 * @author Vladimir Velinov
 * @since Oct 12, 2015
 *
 */
public class AircraftFileChooserPanel extends AbsFileChooserPanel {

  private JLabel label;
  private JLabel fileLabel;
  
  private void _initComponents() {
    this.label     = new JLabel(UIConstants.LBL_AIRCRAFT_FILE);
    this.fileLabel = new JLabel();
  }
  
  @Override
  protected Dimension getPanelSize() {
    return new Dimension(1, 50);
  }

  @Override
  protected Dimension getFileBtnSize() {
    return new Dimension(120, 25);
  }

  @Override
  protected String getBorderTitle() {
    return UIConstants.BORDER_AIRCRAFT;
  }

  @Override
  protected String getFileBtnLabel() {
    return UIConstants.LBL_FILE_CHOOSE_BTN;
  }

  @Override
  protected List<JComponent> getLeftPanelComponents() {
    List<JComponent> components;
    
    this._initComponents();
    components = new ArrayList<JComponent>();

    components.add(this.label);
    components.add(this.fileLabel);
    
    return components;
  }

  @Override
  protected void onFileChosen(String _filePath) {
    this.fileLabel.setText(_filePath);
  }

  @SuppressWarnings("nls")
  @Override
  protected String getSavedFileName() {
    return "AircraftPath.txt";
  }

  @Override
  protected String getFileSaverKey() {
    return FilePathSaver.AIRCRAFT_PATH;
  }
}
