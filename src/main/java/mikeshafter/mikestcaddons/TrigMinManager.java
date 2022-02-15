package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.sl.API.Variables;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TrigMinManager {
  private final static List<TrigMin> trigMins = new CopyOnWriteArrayList<>();
  
  // This method runs every 1200 ticks, or 60s.
  public static void setTrigMins() {
    for (TrigMin trigMin : trigMins) {
      trigMin.nextMin();
      Variables.get(trigMin.getVariable()).set(trigMin.getMin()+" min");
      Variables.get(trigMin.getVariable()+"T").set(trigMin.getMin()+" min");
      
      if (trigMin.getMin() == 0) trigMins.remove(trigMin);
    }
  }
  
  public static void addTrigMin(int min, String variable) {
    trigMins.add(new TrigMin(min, variable));
  }
}
