package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {
  public final SignActionSwap signActionSwap = new SignActionSwap();
  public final SignActionBarrel signActionBarrel = new SignActionBarrel();
  public final SignActionTrigMin signActionTriggerMin = new SignActionTrigMin();
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    SignAction.unregister(signActionBarrel);
    SignAction.unregister(signActionTriggerMin);
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
  }
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, ThrottleManager::throttleTask, 0, 1);
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, TrigMinManager::setTrigMins, 0, 1200);
    SignAction.register(signActionSwap);
    SignAction.register(signActionBarrel);
    SignAction.register(signActionTriggerMin);
    CommandManager manager = new CommandManager();
    Objects.requireNonNull(getCommand("throttle")).setExecutor(manager);
    Objects.requireNonNull(getCommand("throttle")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("door")).setExecutor(manager);
    Objects.requireNonNull(getCommand("door")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("swap")).setExecutor(manager);
    Objects.requireNonNull(getCommand("swap")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("decouple")).setExecutor(manager);
    Objects.requireNonNull(getCommand("decouple")).setTabCompleter(manager);
    this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
}
