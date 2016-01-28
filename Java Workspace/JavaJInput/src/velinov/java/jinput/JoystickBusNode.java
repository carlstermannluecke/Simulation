package velinov.java.jinput;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fr.dgac.ivy.IvyException;
import velinov.java.ivybus.AbsIvyBusNode;
import velinov.java.jinput.Joystick.Axes;


/**
 * @author Vladimir Velinov
 * @since Jan 18, 2016
 */
public class JoystickBusNode extends AbsIvyBusNode 
    implements PropertyChangeListener
{

  /**
   * default constructor.
   */
  @SuppressWarnings("nls")
  public JoystickBusNode() {
    super("LINK", "Joystick joined the bus");
    
  }

  @SuppressWarnings("nls")
  @Override
  public void propertyChange(PropertyChangeEvent _evt) {
    String message;
    Axes   axes;
    float  xAxis;
    float  yAxis;
    float  throttle;
    
    if (!_evt.getPropertyName().equals(Joystick.JOYSTICK_DATA_UPDATED)) {
      return;
    }
    
    axes     = (Axes) _evt.getNewValue();
    
    xAxis    = axes.xAxis * 500;
    yAxis    = axes.yAxis * 500;
    throttle = (1 - axes.throttle) * 10000;
    
    int xAxisInt    = (int) xAxis;
    int yAxisInt    = (int) yAxis;
    int throttleInt = (int) throttle;
    
    message = "1 ROTORCRAFT_FP" + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " " + 0 
        + " " + 0 + " " + xAxisInt + " " + yAxisInt + " " + 0 + " " + 0 + " " + 0 + " " + 0
        + " " + 0 + " " + throttleInt + " " + 0;
    
    try {
      this.ivyBus.sendMsg(message);
    }
    catch (IvyException _e) {
      // TODO Auto-generated catch block
      _e.printStackTrace();
    }
    
  }

}