package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;


public class TrigMin {
  private final String variable;
  private int min;
  private final SignActionEvent event;
  
  public TrigMin(int min, String variable, SignActionEvent event) {
    this.min = min;
    this.variable = variable;
    this.event = event;
  }
  
  public void nextMin() {
    this.min--;
  }
  
  public int getMin() {
    return min;
  }
  
  public String getVariable() {
    return variable;
  }
  
  public SignActionEvent getEvent() {
    return event;
  }
}
