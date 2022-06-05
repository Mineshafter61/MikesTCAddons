package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.signactions.*;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import mikeshafter.mikestcaddons.util.TrigMinManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {
  
  private final SignActionSwap signActionSwap = new SignActionSwap();
  private final SignActionBarrelStation signActionBarrelStation = new SignActionBarrelStation();
  private final SignActionBarrelRun signActionBarrelRun = new SignActionBarrelRun();
  private final SignActionTrigMin signActionTriggerMin = new SignActionTrigMin();
  private final SignActionVariable signActionVariable = new SignActionVariable();
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    SignAction.unregister(signActionBarrelStation);
    SignAction.unregister(signActionBarrelRun);
    SignAction.unregister(signActionTriggerMin);
    SignAction.unregister(signActionVariable);
    this.getLogger().log(Level.INFO, "Mike's TC Addons has been disabled!");
    for (Player player : Bukkit.getOnlinePlayers()) ThrottleManager.removeThrottle(player);
  }
  
  public static int getInteger(String str, char axis, CommandSender sender) {
    if ((sender instanceof Player || sender instanceof BlockCommandSender) && str.startsWith("~")) {
      Location loc = sender instanceof Player ? ((Player) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
      int i = Integer.parseInt(str.substring(1));
      switch (axis) {
        case 'x' -> i += loc.getBlockX();
        case 'y' -> i += loc.getBlockY();
        case 'z' -> i += loc.getBlockZ();
      }
      
      return i;
    } else {
      return Integer.parseInt(str);
    }
  }
  
  @Override
  public void onEnable() {
    //
    // Register schedulers
    //
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, ThrottleManager::throttleTask, 0, 1);
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, TrigMinManager::setTrigMins, 0, 1200);
    
    //
    // Register SignActions
    //
    SignAction.register(signActionSwap);
    SignAction.register(signActionBarrelStation);
    SignAction.register(signActionTriggerMin);
    SignAction.register(signActionVariable);
    SignAction.register(signActionBarrelRun);
    Commands manager = new Commands();
    Objects.requireNonNull(getCommand("throttle")).setExecutor(manager);
    Objects.requireNonNull(getCommand("door")).setExecutor(manager);
    Objects.requireNonNull(getCommand("swap")).setExecutor(manager);
    Objects.requireNonNull(getCommand("decouple")).setExecutor(manager);
    Objects.requireNonNull(getCommand("opengate")).setExecutor(manager);
    this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);
    Objects.requireNonNull(getCommand("throttle")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("door")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("swap")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("decouple")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("opengate")).setTabCompleter(manager);
    this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
  }
}
