package mikeshafter.mikestcaddons.dynamics;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import mikeshafter.mikestcaddons.MikesTCAddons;
import mikeshafter.mikestcaddons.util.AddonsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.sign.Side;
import java.util.ArrayList;
import java.util.List;

public class SignActionPSD extends SignAction {

@Override
public boolean match (SignActionEvent info) {
	return info.isType("psd", "platformdoor", "traindoor", "door");
}

@Override
public void execute (SignActionEvent info) {
	if (!info.isAction(SignActionType.REDSTONE_CHANGE, SignActionType.GROUP_ENTER, SignActionType.GROUP_LEAVE)) return;

	// Parse scan size
	String[] posA = info.getLine(2).split("/");
	String[] posB = info.getLine(3).split("/");
	Location l = info.getAttachedBlock().getLocation();
	int[] xyz = new int[]{l.getBlockX(), l.getBlockY(), l.getBlockZ()};
	int[] a = new int[3];
	int[] b = new int[3];
	for (int i = 0; i < 3; ++i) {
		a[i] = AddonsUtil.parseRelative(posA[i], xyz[i]);
		b[i] = AddonsUtil.parseRelative(posB[i], xyz[i]);
	}

	// Get current block, and scan from there
	List<Sign> signs = getSigns(l.getWorld(), a, b);

	if (info.isAction(SignActionType.REDSTONE_CHANGE) && info.hasRailedMember()) {
		for (Sign sign : signs) executePSDSign(sign, info.isPowered());
	}
}

@Override
public boolean build (SignChangeActionEvent event) {
	return SignBuildOptions.create().setName("train door and platform door opener").setDescription("automatically open train and platform doors").handle(event.getPlayer());
}

private List<Sign> getSigns (World w, int[] posA, int[] posB) {
	List<Sign> signs = new ArrayList<>();
	int[] small = new int[]{Math.min(posA[0], posB[0]), Math.min(posA[1], posB[1]), Math.min(posA[2], posB[2])};
	int[] large = new int[]{Math.max(posA[0], posB[0]), Math.max(posA[1], posB[1]), Math.max(posA[2], posB[2])};
	for (int x = small[0]; x <= large[0]; ++x) {
		for (int y = small[1]; y <= large[1]; ++y) {
			for (int z = small[2]; z <= large[2]; ++z) {
				if (new Location(w, x, y, z).getBlock().getState() instanceof Sign sign) {
					List<String> lines = AddonsUtil.parseComponents(sign.getSide(Side.FRONT).lines());
					if (lines.get(0).equalsIgnoreCase("[psd]")) signs.add(sign);
				}
			}
		}
	}
	return signs;
}

private void executePSDSign (Sign sign, boolean open) {
	List<String> lines = AddonsUtil.parseComponents(sign.getSide(Side.FRONT).lines());
	if (!lines.get(0).equalsIgnoreCase("[psd]")) return;

	var l = AddonsUtil.getReferenceBlock(sign).getLocation();
	int x = l.getBlockX();
	int y = l.getBlockY();
	int z = l.getBlockZ();
	var w = l.getWorld();

	if (open) {
		long openTicks = AddonsUtil.parseTicks(lines.get(2));
		var dir = Direction.parse(lines.get(3)).getDirection(AddonsUtil.getSignFacing(sign));

		for (int i = 1; i <= 5; i++) {
			var location = new Location(w, x, y + i, z);
			// open for 5 minutes if no time is set
			AddonsUtil.openDoor(location, dir, openTicks > 0 && openTicks < 6000 ? openTicks : 6000);

			// TODO: if openticks > 0 but the door is closed before the timer ends, DO NOT continue with flashLamp(location, openTicks)!!!
			if (openTicks > 0) {flashLamp(location, openTicks);}
			else {flashLamp(location, true);}
		}
	}
	else {
		for (int i = 1; i <= 5; i++) {
			var location = new Location(w, x, y + i, z);
			location.add(0d, 1d, 0d);
			AddonsUtil.closeDoor(location);
			flashLamp(location, false);
		}
	}
}

private void flashLamp (Location location, long openTicks) {
	Block block = location.getBlock();
	if (block.getState().getBlockData() instanceof Lightable lightable) {
		Runnable flashRun = () -> {
			lightable.setLit(!lightable.isLit());
			block.setBlockData(lightable);
		};

		// opening
		var open = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), flashRun, 0, 5);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), open::cancel, 40);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			lightable.setLit(true);
			block.setBlockData(lightable);
		}, 45);

		// closing
		var close = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), flashRun, openTicks + 45, 5);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), close::cancel, openTicks + 85);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			lightable.setLit(false);
			block.setBlockData(lightable);
		}, openTicks + 90);
	}
}

private void flashLamp (Location location, boolean endOn) {
	Block block = location.getBlock();
	if (block.getState().getBlockData() instanceof Lightable lightable) {
		var flashTask = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			lightable.setLit(!lightable.isLit());
			block.setBlockData(lightable);
		}, 0, 5);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), flashTask::cancel, 40);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			lightable.setLit(endOn);
			block.setBlockData(lightable);
		}, 45);
	}
}
}
