package velinov.java.finken.drone.real;

import java.util.ArrayList;
import java.util.List;

import coppelia.CharWA;
import coppelia.FloatWA;
import velinov.java.finken.drone.sensoric.ProxSensorType;
import velinov.java.vrep.VrepClient;
import velinov.java.vrep.VrepConnection;
import velinov.java.vrep.objects.AbsObjectUpdater;


/**
 * An <code>AbsObjectUpdater</code> for the <code>RealFinkenDrone</code>.
 * 
 * @author Vladimir Velinov
 * @since Jul 7, 2015
 */
public class RealFinkenDroneUpdater extends AbsObjectUpdater {
  
  private final List<RealFinkenDrone> realDrones;

  /**
   * default constructor.
   * @param _vrepClient
   */
  public RealFinkenDroneUpdater(VrepClient _vrepClient) {
    super(_vrepClient);
    
    this.realDrones = new ArrayList<RealFinkenDrone>();
  }
  
  /**
   * add a <code>List</code> of <code>RealFinkenDrone</code> to the update list.
   * @param _drones
   */
  @SuppressWarnings("nls")
  public void addRealDrones(List<RealFinkenDrone> _drones) {
    if (_drones == null) {
      throw new NullPointerException("null drones");
    }
    
    this.realDrones.addAll(_drones);
  }
  
  /**
   * add a <code>RealFinkenDrone</code> to the update list.
   * @param _drone
   */
  @SuppressWarnings("nls")
  public void addRealDrone(RealFinkenDrone _drone) {
    if (_drone == null) {
      throw new NullPointerException("null drone");
    }
    this.realDrones.add(_drone);
  }

  @SuppressWarnings("nls")
  @Override
  protected void onObjectUpdate() {
    CharWA  stringData;
    FloatWA distValues;
    int     ret;
    
    stringData = new CharWA(1);
    distValues = new FloatWA(1);
    
    for (RealFinkenDrone drone : this.realDrones) {
      ret = this.vrepConnection.simxGetStringSignal(
          this.vrepClient.getClientId(), "sensor_dist" + 
          drone.getObjectName().getNameIndex(), stringData, 
          VrepConnection.simx_opmode_oneshot_wait);

      if (ret != VrepConnection.simx_return_ok && 
          ret != VrepConnection.simx_return_novalue_flag) 
      {
        throw new IllegalStateException("error in return code");
      }
      
      distValues.initArrayFromCharArray(stringData.getArray());
     
      /*
      if (drone.getObjectName().getNameIndex().equals("")) {
        System.out.println("sensor_dist " + distValues.getArray()[0]);
      }
      else if (drone.getObjectName().getNameIndex().equals("0")) {
        System.out.println("sensor_dist0 " + distValues.getArray()[0]);
      }
      */
      
      for (int i = 0; i < distValues.getArray().length; i ++) {
        drone.setProxSensorValue(ProxSensorType.fromId(i), 
            distValues.getArray()[i]);
      }
      
      //System.out.println(drone.getProxSensorValue(ProxSensorType.FRONT));
      
    }
  }

}