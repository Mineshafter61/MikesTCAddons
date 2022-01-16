package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.door.SignActionSwap;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {
  public final SignActionSwap signActionSwap = new SignActionSwap();
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, ThrottleManager::throttleTask, 0, 1);
    SignAction.register(signActionSwap);
    Objects.requireNonNull(getCommand("throttle")).setExecutor(new CommandManager());
    Objects.requireNonNull(getCommand("door")).setExecutor(new CommandManager());
    Objects.requireNonNull(getCommand("swap")).setExecutor(new CommandManager());
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
  }
}
