package velinov.java.finken.drone.virtual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.SAXException;

import velinov.java.finken.drone.FinkenDrone;
import velinov.java.ivybus.message.Message;


/**
 * Describes a virtual finken drone.
 * 
 * @author Vladimir Velinov
 * @since 17.05.2015
 */
public interface VirtualFinkenDrone extends FinkenDrone, Runnable {
  
  /**
   * @return the flight time in seconds.
   */
  public double getFlightTime();
  
  /**
   * starts a <code>Thread</code> that publishes the <code>Message</code>s on 
   * the Ivy Bus.
   */
  public void startPublish();
  
  /**
   * stops the <code>Thread</code> that publishes the <code>Message</code>s on 
   * the Ivy Bus.
   */
  public void stopPublish();
  
  /**
   * @return {@code true} if the {@code VirtualFinkenDrone} is currently
   *     publishing {@link Message}s on the IvyBus. 
   */
  public boolean isPublishing();

  
  /**
   * loads the telemetry from an xml file.
   * 
   * @param _telemetryXml
   * @throws SAXException 
   * @throws FileNotFoundException 
   * @throws IOException 
   */
  public void loadTelemetryData(File _telemetryXml) 
      throws FileNotFoundException, SAXException, IOException;
  
}
