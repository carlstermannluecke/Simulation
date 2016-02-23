package velinov.java.finken.app.ui.tabs.settings;

import javax.swing.Box;
import javax.swing.BoxLayout;

import velinov.java.finken.app.ui.tabs.AbsTab;

/**
 * a <code>JPanel</code> defining the view for the setting tab 
 * in the <code>FinkenSimBridgeView</code>. 
 *
 * @author Vladimir Velinov
 * @since Oct 11, 2015
 *
 */
public class SettingsTab extends AbsTab {

  @SuppressWarnings("nls")
  private static final String TAB_LABEL = "Settings";
  
  private final TelemetryFileChooserPanel telemetryPanel;
  private final AircraftFileChooserPanel  aircraftPanel;
  private final TelemetryModeChooserPanel modePanel;
  
  /**
   * default constructor.
   */
  public SettingsTab() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
    this.telemetryPanel = new TelemetryFileChooserPanel();
    this.aircraftPanel  = new AircraftFileChooserPanel();
    this.modePanel      = new TelemetryModeChooserPanel();
    
    this.telemetryPanel.addPropertyChangeListener(this.modePanel);
    
    this.add(this.aircraftPanel.getPanel());
    this.add(this.telemetryPanel.getPanel());
    this.add(this.modePanel.getPanel());
    
    this.add(Box.createVerticalStrut(540));
  }
  
  @Override
  public String getTabLabel() {
    return SettingsTab.TAB_LABEL;
  }
  
  /**
   * @return the <code>TelemetryFileChooserPanel</code>.
   */
  public TelemetryFileChooserPanel getTelemetryChooserPanel() {
    return this.telemetryPanel;
  }

}