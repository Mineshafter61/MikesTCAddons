package mikeshafter.oldthrottlereloaded;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;


public final class OldThrottleReloaded extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Throttle::throttleTask, 0, 1);
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
  }
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (command.getName().equalsIgnoreCase("throttle") && sender instanceof Player player) {
      if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
        Throttle.addPlayer(player);
        return true;
      }
      else if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
        Throttle.removePlayer(player);
        return true;
      }
    }
    
    return false;
  }
}
