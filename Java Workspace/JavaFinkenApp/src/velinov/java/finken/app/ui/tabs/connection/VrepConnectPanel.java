package velinov.java.finken.app.ui.tabs.connection;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import velinov.java.finken.app.ui.AbsConnectPanel;
import velinov.java.finken.app.ui.UIConstants;
import velinov.java.vrep.StandardVrepServer;
import velinov.java.vrep.VrepClient;

/**
 * An {@link AbsConnectPanel} that manages the connection to the V-REP.
 * 
 * @author Vladimir Velinov
 * @since 25.04.2015
 */
public class VrepConnectPanel extends AbsConnectPanel {
  
  @SuppressWarnings("nls")
  private static final String    IP              = "127.0.0.1";
  @SuppressWarnings("nls")
  private static final String    PORT            = "19999";
  
  private static final Dimension IP_FIELD_SIZE   = new Dimension(100, 20);
  private static final Dimension PORT_FIELD_SIZE = new Dimension(50, 20);

  private JLabel     ipLabel;
  private JLabel     portLabel;
  private JTextField ipField;
  private JTextField portField;
  
  
  /**
   * default constructor.
   */
  public VrepConnectPanel() {
    //this._initComponents();
  }
  
  private void _initComponents() {
    this.ipLabel   = new JLabel(UIConstants.LBL_VREP_IP);
    this.portLabel = new JLabel(UIConstants.LBL_VREP_PORT);
    
    this.ipField   = new JTextField(IP);
    this.portField = new JTextField(PORT);
    
    this.ipField.setPreferredSize(IP_FIELD_SIZE);
    this.ipField.setHorizontalAlignment(SwingConstants.CENTER);
    
    this.portField.setPreferredSize(PORT_FIELD_SIZE);
    this.portField.setHorizontalAlignment(SwingConstants.CENTER);
  }

  @Override
  protected Dimension getPanelSize() {
    return new Dimension(1, 50);
  }

  @Override
  protected Dimension getConnectBtnSize() {
    return new Dimension(120, 25);
  }

  @Override
  protected String getBorderTitle() {
    return UIConstants.BORDER_VREP;
  }

  @Override
  protected String getBtnConnectedLabel() {
    return UIConstants.LBL_VREP_DISCONNECT_BTN;
  }
  
  @Override
  protected String getBtnDisconnectedLabel() {
    return UIConstants.LBL_VREP_CONNECT_BTN;
  }

  @Override
  protected Object getConnectObject() {
    String ip   = this.ipField.getText().trim();
    int    port = Integer.parseInt(this.portField.getText().trim());
    
    return new StandardVrepServer(ip, port);
  }

  @Override
  protected String getPropertyConnected() {
    return VrepClient.PROPERTY_CONNECTED;
  }

  @Override
  protected String getPropertyDisconnected() {
    return VrepClient.PROPERTY_DISCONNECTED;
  }

  @Override
  protected List getLeftPanelComponents() {
    List<JComponent> components;
    
    components = new ArrayList<JComponent>();
    
    this._initComponents();
    components.add(this.ipLabel);
    components.add(this.ipField);
    components.add(this.portLabel);
    components.add(this.portField);
    
    return components;
  }
  
}