package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.attachments.SignActionAttachment;
import mikeshafter.mikestcaddons.attachments.SignActionSwap;
import mikeshafter.mikestcaddons.dynamics.SignActionPSD;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import mikeshafter.mikestcaddons.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class MikesTCAddons extends JavaPlugin {

private final SignActionSwap signActionSwap = new SignActionSwap();
	private final SignActionPSD signActionRHStation = new SignActionPSD();
	private final SignActionAttachment signActionAttachment = new SignActionAttachment();

	@Override
public void onDisable() {
	// Plugin shutdown logic
	SignAction.unregister(signActionSwap);
		SignAction.unregister(signActionRHStation);
		SignAction.unregister(signActionAttachment);

	Util.gates.forEach((gate) -> gate.closeGate(true));
	this.getLogger().log(Level.INFO, "Mike's TC Addons has been disabled!");
	for (Player player : Bukkit.getOnlinePlayers()) ThrottleManager.removeThrottle(player);
}


@Override
public void onEnable() {
	this.saveDefaultConfig();

	Commands commands = new Commands();
	commands.enable(this);

	SignAction.register(signActionSwap);
	SignAction.register(signActionAttachment);
	SignAction.register(signActionRHStation);

	this.getServer().getPluginManager().registerEvents(new ThrottleManager(), this);

	this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
}
}
