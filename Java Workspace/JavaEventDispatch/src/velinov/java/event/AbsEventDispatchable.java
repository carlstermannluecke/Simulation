package velinov.java.event;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * an abstract implementation of {@link EventDispatchable}.
 * 
 * @author Vladimir Velinov
 * @since 18.03.2015
 *
 */
public class AbsEventDispatchable implements EventDispatchable {
  
  protected final PropertyChangeSupport listeners;
  
  protected AbsEventDispatchable() {
    this.listeners = new PropertyChangeSupport(this);
  }

  @SuppressWarnings("nls")
  @Override
  public void addPropertyChangeListener(PropertyChangeListener _listener) {
    if (_listener == null) {
      throw new NullPointerException("Listener is null");
    }
    
    this.listeners.addPropertyChangeListener(_listener);
  }

  @SuppressWarnings("nls")
  @Override
  public void addPropertyChangeListener(String _property,
      PropertyChangeListener _listener)
  {
    if (_listener == null) {
      throw new NullPointerException("Listener is null");
    }
    
    if (_property == null || _property.isEmpty()) {
      throw new IllegalArgumentException("property null or empty");
    }
    
    this.listeners.addPropertyChangeListener(_property, _listener);
  }
  
  @Override
  public void addPropertyChangeListener(PropertyChangeListener _listener,
      String... _strings)
  {
    for (int i = 0; i < _strings.length; i ++) {
      this.addPropertyChangeListener(_strings[i], _listener);
    }
  }

  @SuppressWarnings("nls")
  @Override
  public void removePropertyChangeListener(PropertyChangeListener _listener) {
    if (_listener == null) {
      throw new NullPointerException("Listener is null");
    }
    
    this.listeners.removePropertyChangeListener(_listener);
  }

  @SuppressWarnings("nls")
  @Override
  public void removePropertyChangeListener(String _property,
      PropertyChangeListener _listener)
  {
    if (_property == null || _property.isEmpty()) {
      throw new IllegalArgumentException("property null or empty");
    }
    
    if (_listener == null) {
      throw new NullPointerException("Listener is null");
    }
    
    this.removePropertyChangeListener(_property, _listener);
  }
  
  @SuppressWarnings("nls")
  protected void firePropertyChange(String _property, Object _oldValue, 
      Object _newValue) 
  {
    if (_property == null || _property.isEmpty()) {
      throw new IllegalArgumentException("property null or empty");
    }
    
    this.listeners.firePropertyChange(_property, _oldValue, _newValue);
  }
  
  @SuppressWarnings("nls")
  protected void fireBooleanPropertyChanged(String _property, boolean _value) {
    if (_property == null || _property.isEmpty()) {
      throw new IllegalArgumentException("property null or empty");
    }
    
    this.listeners.firePropertyChange(_property, !_value, _value);
  }


}
