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
    CommandManager manager = new CommandManager();
    Objects.requireNonNull(getCommand("throttle")).setExecutor(manager);
    Objects.requireNonNull(getCommand("throttle")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("door")).setExecutor(manager);
    Objects.requireNonNull(getCommand("door")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("swap")).setExecutor(manager);
    Objects.requireNonNull(getCommand("swap")).setTabCompleter(manager);
    this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
  }
}
