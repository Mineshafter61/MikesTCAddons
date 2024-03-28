package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.signactions.SignActionRHStation;
import mikeshafter.mikestcaddons.signactions.SignActionSwap;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import mikeshafter.mikestcaddons.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {

private final SignActionSwap signActionSwap = new SignActionSwap();
private final SignActionRHStation signActionRHStation = new SignActionRHStation();

@Override
public void onDisable() {
	// Plugin shutdown logic
	SignAction.unregister(signActionSwap);
	SignAction.unregister(signActionRHStation);
	Util.gates.forEach((gate) -> gate.closeGate(true));
	this.getLogger().log(Level.INFO, "Mike's TC Addons has been disabled!");
	for (Player player : Bukkit.getOnlinePlayers()) ThrottleManager.removeThrottle(player);
}


@Override
public void onEnable() {
	this.saveDefaultConfig();
	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, ThrottleManager::throttleTask, 0, 1);

	MapResourcePack.VANILLA.load();
	MapResourcePack.SERVER.load();

	SignAction.register(signActionSwap);
	SignAction.register(signActionRHStation);
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
