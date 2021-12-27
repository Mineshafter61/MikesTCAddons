package mikeshafter.oldthrottlereloaded;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public final class OldThrottleReloaded extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
  }
}
