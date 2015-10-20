package velinov.java.finken.drone.virtual;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;

import fr.dgac.ivy.IvyException;
import velinov.java.finken.aircraft.Aircraft;
import velinov.java.finken.drone.AbsFinkenDrone;
import velinov.java.finken.messages.VirtualMessage;
import velinov.java.finken.telemetry.Telemetry;
import velinov.java.finken.telemetry.TelemetryXmlReader;
import velinov.java.vrep.objects.Shape;

/**
 * Represent a virtual {@link AbsFinkenDrone}.
 * 
 * @author Vladimir Velinov
 * @since 27.04.2015
 */
public class StandardVirtualFinkenDrone extends AbsFinkenDrone 
    implements VirtualFinkenDrone 
{ 
  /**
   * Represents the base name of all virtual FINKEN drones.
   */
  @SuppressWarnings("nls")
  public static final String VIRTUAL_FINKEN_DRONE_NAME = "VirtualFinkenDrone";
  
  /**
   * the default step in milliseconds
   */
  public static final long   STEP_MILLISECONDS         = 100;
  
  /**
   * the default step in seconds
   */
  public static final double STEP_SECONDS              = 
      STEP_MILLISECONDS / 1000;
  
  /*
   * TODO Aircraft is common for Virtual and Real drone 
   * -> move it to AbsFinkenDrone.
   */
  private final Aircraft                    aircraft;
  private final VirtualDroneBusNode         busNode;
  private Telemetry                         telemetry;
  private Thread                            thread;
  private Map<Double, List<VirtualMessage>> timeMap;
  private Double[]                          schedule;
  private double                            flightTime;
  private volatile boolean                  publish;
  
  /**
   * @param _aircraft 
   * @param _shape
   * @param _id
   */
  public StandardVirtualFinkenDrone(Aircraft _aircraft, Shape _shape, int _id) {
    super(_shape, _aircraft, _id);
    
    this.aircraft = _aircraft;
    this.busNode  = new VirtualDroneBusNode(this.aircraft);
  }
  
  @Override
  public double getFlightTime() {
    return this.flightTime;
  }
  
  @Override
  public void joinIvyBus() {
    this.busNode.connect();
  }

  @Override
  public void leaveIvyBus() {
    this.busNode.disconnect();
  }

  @Override
  public boolean isConnectedToBus() {
    return this.busNode.isConnected();
  }

  @SuppressWarnings("nls")
  @Override
  public void loadTelemetryData(File _telemetryXml) 
      throws SAXException, IOException 
  {
    final TelemetryXmlReader reader;
    final Set<Double>        set;
    
    if (this.publish) {
      return;
    }
    
    reader = new TelemetryXmlReader(_telemetryXml, "vrep");
    reader.parseXmlDocument();
    
    this.telemetry  = reader.getTelemetry();
    this.telemetry.initTimeMap();
    this.timeMap    = this.telemetry.getTimeMap();
    
    // init schedule
    set             = this.timeMap.keySet();
    this.schedule   = set.toArray(new Double[set.size()]);
    Arrays.sort(this.schedule);
    
  }
  
  @Override
  public void startPublish() {
    if (this.publish) {
      return;
    }
    this.thread  = new Thread(this);
    this.thread.start();
    this.publish = true;
  }

  @Override
  public void stopPublish() {
    if (!this.publish) {
      return;
    }
    this.publish = false;
    this.thread  = null;
  }

  @Override
  public boolean isPublishing() {
    return this.publish;
  }

  @Override
  public void run() {
    int counter        = 0;
    int scheduleLength = this.schedule.length;
    
    while (this.publish) {
      counter         += 1;
      this.flightTime += STEP_SECONDS;
      
      try {
        // 0.1 sec step
        Thread.sleep(STEP_MILLISECONDS);
      }
      catch (InterruptedException _e) {
        // ignored
      }
      
      for (int i = 0; i < scheduleLength; i ++) {
        double time = this.schedule[i];
        double period  = (time * 10);
        
        if (counter % period == 0) {
          List<VirtualMessage> dueMessages = this.timeMap.get(time);
          for (VirtualMessage msg : dueMessages) {
            try {
              msg.buildMessage(this);
              this.busNode.publishMessage(msg.getMessage());
            }
            catch (IvyException _e) {
              this.stopPublish();
            }
          }
        }
      }
      
    }
  }

}
