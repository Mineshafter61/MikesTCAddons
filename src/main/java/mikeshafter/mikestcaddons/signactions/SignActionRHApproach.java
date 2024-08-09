package mikeshafter.mikestcaddons.signactions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import mikeshafter.mikestcaddons.MikesTCAddons;


public class SignActionRHApproach extends SignAction {

@Override public boolean match (SignActionEvent info) { return info.isType("rhapproach", "advapproach", "barrelrun"); }

@Override public void execute (SignActionEvent info) {
	/*
	[train]
	rhapproach
	<name> <platform>
	<toc>
	 */

	if ((info.isTrainSign() || info.isCartSign())
		&& info.isAction(SignActionType.GROUP_ENTER, SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
		&& info.isPowered()) {

		// Get the direction in which the carts are entering
		var enterFace = info.getCartEnterFace();
		// Get the direction in which the sign is facing
		var facingDir = info.getFacing();
		// get train config
		ConfigurationNode trainConfig = info.getGroup().getProperties().getConfig();

		// get train company's announcement format
		String operator = trainConfig.get("operator", String.class);
		MikesTCAddons plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
		FileConfiguration config = new FileConfiguration(plugin, "config.yml");
		var sequence = config.getNode("announcement-format."+operator);

	}

}
@Override public boolean build (SignChangeActionEvent event) {
	return SignBuildOptions.create()
		.setName("approach details")
		.setDescription("tells the train what to do on approach to a station")
		.handle(event.getPlayer());
}
}

