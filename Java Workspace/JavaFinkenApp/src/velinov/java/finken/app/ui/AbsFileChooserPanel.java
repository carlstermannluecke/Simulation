package velinov.java.finken.app.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import velinov.java.bean.AbsEventDispatchable;
import velinov.java.finken.app.filesaver.FilePathSaver;
import velinov.java.finken.app.filesaver.FilePathSaverUtils;


/**
 * defines an abstract <code>JPanel</code> used for choosing a file from the 
 * file system.
 * 
 * @author Vladimir Velinov
 * @since Oct 11, 2015
 *
 */
public abstract class AbsFileChooserPanel extends AbsEventDispatchable {
  
  /**
   * a property that is fired when a file has been chosen.
   */
  @SuppressWarnings("nls")
  public final static String FILE_CHOSEN_PROPERTY = "fileChosen";
  
  private final JPanel             mainPanel;
  private final JPanel             leftPanel;
  private final JPanel             rightPanel;
  
  private final JButton            fileButton;
  private final JFileChooser       fileChooser;
  private final FilePathSaver      fileSaver;
  private final FilePathSaverUtils fileSaverUtils;
  private ConnectRequestor         hadnler;
  private File                     file;
  
  /**
   * default constructor.
   */
  public AbsFileChooserPanel() {
    this.mainPanel      = new JPanel();
    this.leftPanel      = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
    this.rightPanel     = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
    this.fileButton     = new JButton(this.getFileBtnLabel());
    this.fileChooser    = new JFileChooser();
    this.hadnler        = new ConnectRequestor();
    this.fileSaver      = new FilePathSaver(this.getSavedFileName());
    this.fileSaverUtils = FilePathSaverUtils.getInstance();
    
    this.mainPanel.setLayout(new BorderLayout());
    this.mainPanel.setPreferredSize(this.getPanelSize());
    this.mainPanel.setBorder(BorderFactory.createTitledBorder(
        this.getBorderTitle()));
    
    this.rightPanel.add(this.fileButton);
    this._initLeftPanel();
    
    this.fileButton.addActionListener(this.hadnler);
    this.fileButton.setPreferredSize(this.getFileBtnSize());
    
    this.mainPanel.add(this.leftPanel, BorderLayout.WEST);
    this.mainPanel.add(this.rightPanel, BorderLayout.EAST);
    
    this.file = new File(this.fileSaver.getFilePath());
    
    this.onFileChosen(this.fileSaver.getFilePath());
    this.fileChooser.setCurrentDirectory(this.file);
    
    this.fileSaverUtils.addFilePathSaver(
        this.getFileSaverKey(), this.fileSaver);
  }
  
  /**
   * @return the main panel.
   */
  public JPanel getPanel() {
    return this.mainPanel;
  }
  
  protected abstract Dimension getPanelSize();
  
  protected abstract Dimension getFileBtnSize();
  
  protected abstract String getBorderTitle();
  
  protected abstract String getFileBtnLabel();
  
  protected abstract String getSavedFileName();
  
  protected abstract String getFileSaverKey();
  
  protected abstract List<JComponent> getLeftPanelComponents();
  
  protected abstract void onFileChosen(String _filePath);
  
  private void _initLeftPanel() {
    for (JComponent cmp : this.getLeftPanelComponents()) {
      this.leftPanel.add(cmp);
    }
  }
  
  private class ConnectRequestor implements ActionListener {
    AbsFileChooserPanel inst = AbsFileChooserPanel.this;
    @Override
    public void actionPerformed(ActionEvent _event) {
      if (_event.getSource() == AbsFileChooserPanel.this.fileButton) {
        int res;
        res = this.inst.fileChooser.showOpenDialog(null);
        
        if (res == JFileChooser.APPROVE_OPTION) {
          
          this.inst.file = this.inst.fileChooser.getSelectedFile();
          
          this.inst.fileSaver.saveFilePathToFile(this.inst.file);
          
          this.inst.firePropertyChange(FILE_CHOSEN_PROPERTY, 
              null, this.inst.file);
          
          this.inst.onFileChosen(this.inst.file.getAbsolutePath());
        }
        
      }
    }
  }
  
}