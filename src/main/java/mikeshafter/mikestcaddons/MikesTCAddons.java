package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.attachments.SignActionAttachment;
import mikeshafter.mikestcaddons.attachments.SignActionSwap;
import mikeshafter.mikestcaddons.dynamics.SignActionPSD;
import mikeshafter.mikestcaddons.util.Util;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class MikesTCAddons extends JavaPlugin {

private final SignActionSwap signActionSwap = new SignActionSwap();
	private final SignActionPSD signActionPSD = new SignActionPSD();
	private final SignActionAttachment signActionAttachment = new SignActionAttachment();

	@Override
public void onDisable() {
	// Plugin shutdown logic
	SignAction.unregister(signActionSwap);
		SignAction.unregister(signActionPSD);
		SignAction.unregister(signActionAttachment);

		Util.gates.forEach((location, gate) -> gate.closeGate(true));
	this.getLogger().log(Level.INFO, "Mike's TC Addons has been disabled!");
}

@Override
public void onEnable() {
	this.saveDefaultConfig();

	Commands commands = new Commands();
	commands.enable(this);

	SignAction.register(signActionSwap);
	SignAction.register(signActionAttachment);
	SignAction.register(signActionPSD);

	this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
}
}
