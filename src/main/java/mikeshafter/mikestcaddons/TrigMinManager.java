package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.sl.API.Variable;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TrigMinManager {
  private final static List<TrigMin> trigMins = new CopyOnWriteArrayList<>();
  
  public static void setTrigMins() {
    for (TrigMin trigMin : trigMins) {
      SignActionEvent event = trigMin.getEvent();
      trigMin.nextMin();
  
      Variable variable = Variables.get(trigMin.getVariable());
      Variable time = Variables.get(variable+"T");
      Variable destination = Variables.get(variable+"D");
      Variable name = Variables.get(variable+"N");
      Variable speed = Variables.get(variable+"V");
  
      variable.set(trigMin.getMin()+" min");
      time.set(trigMin.getMin()+" min");
      destination.set(event.getMember().getProperties().getDestination());
      name.set(event.getGroup().getProperties().getDisplayName());
      speed.set(String.valueOf(Math.min(MathUtil.round(event.getMember().getRealSpeed(), 2), event.getGroup().getProperties().getSpeedLimit())));
  
      if (trigMin.getMin() == 0) trigMins.remove(trigMin);
    }
  }
  
  public static void addTrigMin(int min, String variable, SignActionEvent event) {
    trigMins.add(new TrigMin(min, variable, event));
  }
}
