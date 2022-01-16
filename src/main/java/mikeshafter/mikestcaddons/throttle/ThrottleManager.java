package mikeshafter.mikestcaddons.throttle;

import org.bukkit.entity.Player;

import java.util.LinkedList;


public class ThrottleManager {
  
  private static LinkedList<Throttle> throttles;
  
  public static void throttleTask() {
    for (Throttle throttle : throttles) {
    
    }
  }
  
  public static void addThrottle(Player player) {
    throttles.add(new Throttle(player));
  }
  
  public static void removeThrottle(Player player) {
    for (Throttle throttle : throttles) {
      if (throttle.getPlayer() == player) throttle.removePlayer();
    }
  }
}
