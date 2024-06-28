package mikeshafter.mikestcaddons.attachments;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

public class SignActionSwap extends SignAction {

	@Override
	public boolean match(SignActionEvent info) {
		return info.isType("swap", "swapdoor");
	}

	@Override
	public void execute(SignActionEvent info) {
		// When a [train] sign is placed, activate when powered by redstone when the train
		// goes over the sign, or when redstone is activated.
		if ((info.isTrainSign() || info.isCartSign())
				&& info.isAction(SignActionType.GROUP_ENTER, SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
				&& info.isPowered()) {
			for (MinecartMember<?> member : info.getMembers()) {
				Swapper a = new Swapper(member, "door_L", "door_R");
				a.run();
			}
		}
	}

	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create()
				.setName("door swapper")
				.setDescription("swaps left and right doors")
				.handle(event.getPlayer());
	}
}
