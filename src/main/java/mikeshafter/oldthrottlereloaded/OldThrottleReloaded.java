package mikeshafter.oldthrottlereloaded;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;


public final class OldThrottleReloaded extends JavaPlugin {
  public final SignActionSwap signActionSwap = new SignActionSwap();
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Throttle::throttleTask, 0, 1);
    SignAction.register(signActionSwap);
    this.getCommand("throttle").setExecutor(new ThrottleCommands());
    this.getCommand("door").setExecutor(new ThrottleCommands());
    this.getCommand("swap").setExecutor(new ThrottleCommands());
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
  }
}
