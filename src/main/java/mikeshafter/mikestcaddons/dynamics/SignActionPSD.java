package mikeshafter.mikestcaddons.dynamics;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import mikeshafter.mikestcaddons.util.Util;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.entity.EntityType;

public class SignActionPSD extends SignAction {
	@Override
	public boolean match(SignActionEvent info) {
		return info.isType("psd", "platformdoor", "traindoor", "door");
	}

	@Override
	public void execute(SignActionEvent info) {
		if ((info.isTrainSign() || info.isCartSign())
				&& info.isAction(SignActionType.GROUP_ENTER, SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)
				&& info.isPowered()
		) {
			Location location = info.getSign().getLocation();
			BlockFace facing = info.getFacing();
			// find glass panes
			Location loc = location.clone();
			for (int i = 1; i <= 5; i++) {
				loc.add(0d, 1d, 0d);
				BlockState state = loc.getBlock().getState();
				if (
						!loc.getWorld().getNearbyEntities(loc, 48, 32, 48, (entity) -> entity.getType() == EntityType.PLAYER).isEmpty()
								&& (state.getBlockData() instanceof Fence || state.getBlockData() instanceof GlassPane)
				) {
					BlockFace dir = facing; // calculate this
					Util.openDoor(loc, dir, Util.parseTicks(info.getLine(2)));
				}
			}
			// relay
		}
	}

	@Override
	public boolean build(SignChangeActionEvent event) {
		return SignBuildOptions.create()
				.setName("train door and platform door opener")
				.setDescription("automatically open train and platform doors")
				.handle(event.getPlayer());
	}
}
