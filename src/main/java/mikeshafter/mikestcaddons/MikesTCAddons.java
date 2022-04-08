package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
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
    this.getLogger().log(Level.INFO, "OldThrottle has been disabled!");
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
    this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);
    this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
  }
}
