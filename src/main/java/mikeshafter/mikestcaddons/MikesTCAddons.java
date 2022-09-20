package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.signactions.SignActionBarrel;
import mikeshafter.mikestcaddons.signactions.SignActionSwap;
import mikeshafter.mikestcaddons.signactions.SignActionTrigMin;
import mikeshafter.mikestcaddons.signactions.SignActionVariable;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import mikeshafter.mikestcaddons.util.BarrelUtil;
import mikeshafter.mikestcaddons.util.TrigMinManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {
  
  private final SignActionSwap signActionSwap = new SignActionSwap();
  private final SignActionBarrel signActionBarrelStation = new SignActionBarrel();
  private final SignActionTrigMin signActionTriggerMin = new SignActionTrigMin();
  private final SignActionVariable signActionVariable = new SignActionVariable();
  
  @Override
  public void onDisable() {
    // Plugin shutdown logic
    SignAction.unregister(signActionSwap);
    SignAction.unregister(signActionBarrelStation);
    SignAction.unregister(signActionTriggerMin);
    SignAction.unregister(signActionVariable);
    BarrelUtil.gates.forEach((gate) -> gate.closeGate(true));
    this.getLogger().log(Level.INFO, "Mike's TC Addons has been disabled!");
    for (Player player : Bukkit.getOnlinePlayers()) ThrottleManager.removeThrottle(player);
  }
  
  
  @Override
  public void onEnable() {
    //
    // Register config
    //
    this.saveDefaultConfig();
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
    Commands manager = new Commands();
    Objects.requireNonNull(getCommand("throttle")).setExecutor(manager);
    Objects.requireNonNull(getCommand("door")).setExecutor(manager);
    Objects.requireNonNull(getCommand("swap")).setExecutor(manager);
    Objects.requireNonNull(getCommand("decouple")).setExecutor(manager);
    Objects.requireNonNull(getCommand("opengate")).setExecutor(manager);
    Objects.requireNonNull(getCommand("reload")).setExecutor(manager);
    this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);
    Objects.requireNonNull(getCommand("throttle")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("door")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("swap")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("decouple")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("opengate")).setTabCompleter(manager);
    Objects.requireNonNull(getCommand("reload")).setTabCompleter(manager);
    this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
  }
}
