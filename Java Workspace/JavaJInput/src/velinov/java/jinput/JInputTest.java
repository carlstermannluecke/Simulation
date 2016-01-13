package velinov.java.jinput;

import javax.swing.JFrame;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller.Type;
import net.java.games.input.test.ControllerReadTest;

public class JInputTest {
  
  private final ControllerEnvironment environment;
  private Joystick                    joystick;
  private JoystickBusNode             busNode;
  private JFrame                      frame;
  
  public JInputTest() {
    Controller[] controllers;
    
    this.environment = ControllerEnvironment.getDefaultEnvironment();
    controllers      = this.environment.getControllers();
    this.busNode     = new JoystickBusNode();
    this.frame       = new JFrame();
    this.frame.setBounds(0, 0, 50 , 50);
    
    this.frame.show();
    
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    for (Controller controller : controllers) {
      if (controller.getType() == Type.STICK) {
        this.joystick = new Joystick(controller);
        this.joystick.addPropertyChangeListener(this.busNode);
        this.busNode.connect();
        this.joystick.start();
        
        break;
      }
    }
    
  }

  public static void main(String[] args) {
    new JInputTest();
  }

}