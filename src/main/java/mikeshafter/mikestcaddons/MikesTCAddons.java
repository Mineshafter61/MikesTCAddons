package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.properties.registry.TCPropertyRegistry;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.attachments.SignActionAttachment;
import mikeshafter.mikestcaddons.attachments.SignActionSwap;
import mikeshafter.mikestcaddons.rh.RHProperties;
import mikeshafter.mikestcaddons.throttle.ThrottleController;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public final class MikesTCAddons extends JavaPlugin {

private final SignActionSwap signActionSwap = new SignActionSwap();
private final SignActionAttachment signActionAttachment = new SignActionAttachment();

private final Commands commands = new Commands();

@Override
public void onLoad () {
	TCPropertyRegistry propertyRegistry = new TCPropertyRegistry(TrainCarts.plugin, commands.getHandler());
	propertyRegistry.registerAll(RHProperties.class);
}

@Override
public void onDisable() {
	// Plugin shutdown logic
		SignAction.unregister(signActionSwap);
		SignAction.unregister(signActionAttachment);

	this.getLogger().log(Level.INFO, "Mike's TC Addons has been disabled!");
}

@Override
public void onEnable() {
	this.saveDefaultConfig();
	this.commands.enable(this);

	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, ThrottleController::run, 0, 1);

	SignAction.register(signActionSwap);
	SignAction.register(signActionAttachment);
	PacketUtil.addPacketListener(this, new ThrottleController(), PacketType.IN_STEER_VEHICLE);

	this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
}
}