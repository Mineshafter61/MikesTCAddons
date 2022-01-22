package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.sl.API.Variable;
import com.bergerkiller.bukkit.sl.API.Variables;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TrigMinManager {
  private final static List<TrigMin> trigMins = new CopyOnWriteArrayList<>();
  
  public static void setTrigMins() {
    for (TrigMin trigMin : trigMins) {
      trigMin.nextMin();
      Variable variable = Variables.get(trigMin.getVariable());
      variable.set(trigMin.getMin()+" min");
      if (trigMin.getMin() == 0) trigMins.remove(trigMin);
    }
  }
  
  public static void addTrigMin(int min, String variable) {
    trigMins.add(new TrigMin(min, variable));
  }
}
