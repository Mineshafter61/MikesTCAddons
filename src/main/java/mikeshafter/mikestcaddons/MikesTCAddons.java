package mikeshafter.mikestcaddons;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import mikeshafter.mikestcaddons.attachments.SignActionAttachment;
import mikeshafter.mikestcaddons.signactions.SignActionRHStation;
import mikeshafter.mikestcaddons.signactions.SignActionSwap;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import mikeshafter.mikestcaddons.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;
import java.util.logging.Level;


public final class MikesTCAddons extends JavaPlugin {

private final SignActionSwap signActionSwap = new SignActionSwap();
private final SignActionRHStation signActionRHStation = new SignActionRHStation();
	private final SignActionAttachment signActionAttachment = new SignActionAttachment();

	private void registerCommands(Commands commands) {
		final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
		PaperCommandManager<CommandSender> manager;
		try {
			manager = new PaperCommandManager<>(this, executionCoordinatorFunction, Function.identity(), Function.identity());
		} catch (Exception e) {
			this.getLogger().log(Level.SEVERE, "Failed to create command manager:");
			this.getLogger().warning(e.getLocalizedMessage());
			return;
		}
		//if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {manager.registerBrigadier();}
		parseCommandAnnotations(commands, manager);
	}

	private void parseCommandAnnotations(Commands commands, PaperCommandManager<CommandSender> manager) {
		final Function<ParserParameters, CommandMeta> commandMetaFunction = p -> CommandMeta.simple().with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "Description not specified.")).build();
		AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class, commandMetaFunction);
		annotationParser.parse(commands);
	}


	@Override
public void onDisable() {
	// Plugin shutdown logic
	SignAction.unregister(signActionSwap);
		SignAction.unregister(signActionAttachment);
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
	SignAction.register(signActionAttachment);

	registerCommands(new Commands());

	this.getLogger().log(Level.INFO, "Mike's TC Addons has been enabled!");
}
}
