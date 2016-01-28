package velinov.java.finken.drone.real;

import java.util.List;

import fr.dgac.ivy.IvyException;
import velinov.java.finken.aircraft.Aircraft;
import velinov.java.finken.drone.sensoric.FinkenProxSensor;
import velinov.java.ivybus.AbsIvyBusNode;
import velinov.java.ivybus.message.Message;


/**
 * An {@link AbsIvyBusNode} that subscribes to the messages from the real
 * quadrocopters and updates the real quadrocopter model in VREP.
 * 
 * @author Vladimir Velinov
 * @since May 25, 2015
 */
public class RealDroneBusNode extends AbsIvyBusNode {
  
  @SuppressWarnings("nls")
  private static final String NODE_TITLE = "VREP_BRIDGE";
  
  private final Aircraft aircraft;
  private final int      ac_id;
  
  /**
   * default constructor.
   * 
   * @param _aircraft 
   */
  @SuppressWarnings("nls")
  public RealDroneBusNode(Aircraft _aircraft) {
    super(NODE_TITLE, _aircraft.getName() + " joined the bus");
    
    this.aircraft = _aircraft;
    this.ac_id    = this.aircraft.getId();
  }
  
  /**
   * Publish a {@link Message} to the Ivy bus.
   * @param _message 
   * @throws IvyException 
   */
  @SuppressWarnings("nls")
  public void publishMessage(Message _message) throws IvyException {
    
    /*
     * be careful stil not implemented!
     */
    throw new IllegalStateException("not implemented yet");
  }
  
  /**
   * send the proximity sensor values as a message on the Ivy-bus.
   * 
   * @param _proxSensors
   * @throws IvyException 
   */
  @SuppressWarnings("nls")
  public void publishProxSensMessage(List<FinkenProxSensor> _proxSensors) 
      throws IvyException 
  {
    if (_proxSensors == null) {
      throw new NullPointerException("null sensors");
    }
    
    String msg;
    
    //msg = this.ac_id + " " + "SONAR_UPLINK" + " ";
    msg = "dl" + " " + "SONAR_UPLINK" + " " + this.ac_id + " " + "255";
    
    for (FinkenProxSensor sensor : _proxSensors) {
      // multiply by 100 t0 change from meters to centimeters
      int dist   = (int) sensor.getDistance();
      String value = String.valueOf(dist);
      
      msg = msg + " " + value;
    }
    
    this.ivyBus.sendMsg(msg);
    
  }
  

}
 