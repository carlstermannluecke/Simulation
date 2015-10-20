package velinov.java.finken.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.xml.sax.SAXException;

import velinov.java.finken.app.ui.FinkenSimBridgeView;
import velinov.java.finken.app.ui.ConnectPanel;
import velinov.java.finken.app.ui.FinkenSimBridgeFrame;
import velinov.java.finken.drone.FinkenDroneScanner;
import velinov.java.finken.drone.real.RealFinkenDrone;
import velinov.java.finken.drone.real.RealFinkenDroneUpdater;
import velinov.java.finken.drone.virtual.VirtualFinkenDrone;
import velinov.java.finken.drone.virtual.VirtualFinkenDroneUpdater;
import velinov.java.finken.ivybus.BusNodesDispatcher;
import velinov.java.vrep.StandardVrepClient;
import velinov.java.vrep.VrepClient;
import velinov.java.vrep.VrepConnection;
import velinov.java.vrep.VrepConnectionUtils;
import velinov.java.vrep.VrepServer;
import velinov.java.vrep.scene.VrepScene;
import fr.dgac.ivy.IvyException;

/**
 * a controller, that manages the callbacks from the GUI, connects to V-REP,
 * retrieves, initializes and starts the drones.
 * 
 * @author Vladimir Velinov
 * @since 12.04.2015
 *
 */
public final class FinkenSimBridgeController {
  
  @SuppressWarnings("nls")
  private static final String TELEMETRY_FILE = 
      "/home/ees/paparazzi/conf/telemetry/ultrasound_rotorcraft.xml";
  
  private VrepConnection             vrepConnection;
  private VrepClient                 vrepClient;
  private VrepScene                  vrepScene;
  private VirtualFinkenDroneUpdater  virtualUpdater;
  private RealFinkenDroneUpdater     realUpdater;
  private BusNodesDispatcher         busDispatcher;
  
  private List<VirtualFinkenDrone>   virtualDrones;
  private List<RealFinkenDrone>      realDrones;
  
  // GUI listeners
  private VrepConnectRequest         vrepConnectRequestor;
  
  // GUI components
  private final FinkenSimBridgeFrame frame;
  private final FinkenSimBridgeView  view;
  
  /**
   * default constuctor.
   */
  public FinkenSimBridgeController() {
    this.vrepConnection       = VrepConnectionUtils.getConnection();
    this.vrepClient           = new StandardVrepClient(this.vrepConnection);
    this.vrepScene            = new VrepScene(this.vrepClient);
    
    this.busDispatcher        = new BusNodesDispatcher();
    this.vrepConnectRequestor = new VrepConnectRequest();
    this.view                 = new FinkenSimBridgeView();
    this.frame                = new FinkenSimBridgeFrame();
    
    this.vrepClient.addPropertyChangeListener(
        this.view.getVrepConnectionListener());
    
    this.view.addPropertyChangeListener(this.vrepConnectRequestor);
    
    this.frame.setContentPane(this.view.getView());
  }
  
  
  private class VrepConnectRequest implements PropertyChangeListener {
    private FinkenSimBridgeController inst = FinkenSimBridgeController.this;
    @Override
    public void propertyChange(PropertyChangeEvent _event) {
      
      switch (_event.getPropertyName()) {
      
      case ConnectPanel.PROPERTY_CONNECT:
        VrepServer server = (VrepServer) _event.getNewValue();
        this.inst.vrepClient.connectToServer(server);
        
        if (!this.inst.vrepClient.isConnected()) {
          return;
        }
        
        try {
          if (this.inst.vrepScene.isLoaded()) {
            // dont init again
            return;
          }
          this.inst.vrepScene.loadScene();
          
          // get virtual drones
          this.inst._retrieveVirtualDrones();
          
          // get real drones
          this.inst._retrieveRealDrones();

          this.inst._initVirtualDrones();
          this.inst._initRealDrones();
          
          /*
           * try to force gc to free the memory allocated from xml parsing
           */
          System.gc();
        }
        catch (Exception _e) {
          _e.printStackTrace();
        }
        break;
        
      case ConnectPanel.PROPERTY_DISCONNECT:
        this.inst._stopAllNodes();
        break;
      }
    }
  }
  
  private void _stopAllNodes() {
    if (this.virtualUpdater != null) {
      this.virtualUpdater.stop();
    }
    if (this.realUpdater != null) {
      this.realUpdater.stop();
    }
    
    this.busDispatcher.disconnectAllNodes();
    this.vrepClient.close();
  }
  
  private void _retrieveVirtualDrones() throws SAXException, IOException {
    this.virtualDrones = FinkenDroneScanner.retrieveVirtualDrones(
        this.vrepScene, this.vrepClient);
    
    if (!this.virtualDrones.isEmpty()) {
      this.virtualUpdater = new VirtualFinkenDroneUpdater(this.vrepClient);
      this.virtualUpdater.addVirtualDrones(this.virtualDrones);
      this.virtualUpdater.start();
    }
  }
  
  private void _retrieveRealDrones() throws SAXException, IOException {
    this.realDrones = FinkenDroneScanner.retrieveRealDrones(
        this.vrepScene, this.vrepClient);
    
    if (!this.virtualDrones.isEmpty()) {
      this.realUpdater = new RealFinkenDroneUpdater(this.vrepClient);
      this.realUpdater.addRealDrones(this.realDrones);
      //this.inst.realUpdater.start();
    }
    
  }
  
  private void _initVirtualDrones() throws 
      FileNotFoundException, SAXException, IOException 
  { 
    for (VirtualFinkenDrone drone : this.virtualDrones) {
      drone.joinIvyBus();
      drone.loadTelemetryData(new File(TELEMETRY_FILE));
      drone.startPublish();
    }
  }
  
  private void _initRealDrones() throws 
      FileNotFoundException, SAXException, IOException, IvyException 
  {
    for (RealFinkenDrone drone : this.realDrones) {
      drone.joinIvyBus();
      drone.loadTelemetryData(new File(TELEMETRY_FILE));
      //drone.startPublish();
    }
  }

}
