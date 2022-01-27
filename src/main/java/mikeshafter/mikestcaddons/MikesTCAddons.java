package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {
  private final SignActionSwap signActionSwap = new SignActionSwap();
  private final SignActionBarrel signActionBarrel = new SignActionBarrel();
  private final SignActionTrigMin signActionTriggerMin = new SignActionTrigMin();
  private final SignActionVariable signActionVariable = new SignActionVariable();
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    SignAction.unregister(signActionBarrel);
    SignAction.unregister(signActionTriggerMin);
    SignAction.unregister(signActionVariable);
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
    SignAction.register(signActionVariable);
    CommandManager manager = new CommandManager();
    Objects.requireNonNull(getCommand("throttle")).setExecutor(manager);
    Objects.requireNonNull(getCommand("door")).setExecutor(manager);
    Objects.requireNonNull(getCommand("swap")).setExecutor(manager);
    Objects.requireNonNull(getCommand("decouple")).setExecutor(manager);
    this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);
    this.getLogger().log(Level.INFO, "OldThrottle has been enabled!");
  }
}
