package mikeshafter.mikestcaddons.signactions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import mikeshafter.mikestcaddons.MikesTCAddons;


public class SignActionRHStation extends SignAction {

@Override public boolean match (SignActionEvent info) { return info.isType("rhstation", "advstation", "barrelsta"); }

@Override public void execute (SignActionEvent info) {
	/*
	[train]
	rhstation
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
		// Get the direction to open the doors
		Direction direction = Direction.parse(info.getLine(3));
		var openDoors = direction.getDirection(facingDir, enterFace).getDirection();
		// set up animation
		AnimationOptions animationOptions = new AnimationOptions();
		// play animation
		if (enterFace.getDirection().angle(openDoors) > 0) {
			animationOptions.setName("door_L");
		}
		else if (enterFace.getDirection().angle(openDoors) < 0) {
			animationOptions.setName("door_R");
		}

		// get train config
		ConfigurationNode trainConfig = info.getGroup().getProperties().getConfig();
		if (info.isAction(SignActionType.GROUP_ENTER)) {
			info.getGroup().playNamedAnimation(animationOptions);
			info.getGroup().getProperties().getConfig().set("door_anim", animationOptions);
		}
		else {
			info.getMember().playNamedAnimation(animationOptions);
			info.getMember().getProperties().getConfig().set("door_anim", animationOptions);
		}

		// get train company's announcement format
		String operator = trainConfig.get("operator", String.class);
		MikesTCAddons plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
		FileConfiguration config = new FileConfiguration(plugin, "config.yml");
		var sequence = config.getNode("announcement-format."+operator);

		// get total delay
		long delay = ParseUtil.parseTime(info.getLine(2));


		return;
	}

	if ((info.isTrainSign() || info.isCartSign()) && info.isPowered() && info.isAction(SignActionType.GROUP_LEAVE, SignActionType.MEMBER_LEAVE, SignActionType.REDSTONE_OFF)) {
		Object o = info.getGroup().getProperties().getConfig().get("door_anim");
		if (o instanceof AnimationOptions animationOptions) {
			animationOptions.setSpeed(-1d);
			if (info.isAction(SignActionType.GROUP_LEAVE))
				info.getGroup().playNamedAnimation(animationOptions);
			else
				info.getMember().playNamedAnimation(animationOptions);
		}
	}
}
@Override public boolean build (SignChangeActionEvent event) {
	return SignBuildOptions.create()
		.setName("station details")
		.setDescription("tells the train which station it's at")
		.handle(event.getPlayer());
}
}

