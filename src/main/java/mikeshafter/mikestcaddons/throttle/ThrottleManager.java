package mikeshafter.mikestcaddons.throttle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ThrottleManager implements Listener {
  private final static List<Throttle> throttles = new CopyOnWriteArrayList<>();
  
  // Run the throttle task
  public static void throttleTask() {for (Throttle throttle : throttles) throttle.run();}
  
  // Player enables throttle
  public static void addThrottle(Player player, int powerCars) {throttles.add(new Throttle(player, powerCars));}
  
  // Methods to switch off throttle
  @EventHandler
  public static void onVehicleLeave(VehicleExitEvent event) {if (event.getExited() instanceof Player player) removeThrottle(player);}
  
  // Helper method
  public static void removeThrottle(Player player) {
    for (Throttle throttle : throttles) {
      if (throttle.getPlayer() == player) {
        throttle.removePlayer();
        throttles.remove(throttle);
      }
    }
  }
  
  @EventHandler
  public static void onQuit(PlayerQuitEvent event) {
    removeThrottle(event.getPlayer());
  }
  
  @EventHandler
  public static void onDeath(PlayerDeathEvent event) {
    removeThrottle(event.getPlayer());
  }
  
  // Disallow the dropping of items
  @EventHandler
  public static void onDrop(PlayerDropItemEvent event) {for (Throttle throttle : throttles) if (throttle.getPlayer() == event.getPlayer()) event.setCancelled(true);}
}
