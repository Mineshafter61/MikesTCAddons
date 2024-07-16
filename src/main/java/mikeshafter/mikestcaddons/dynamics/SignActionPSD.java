package mikeshafter.mikestcaddons.dynamics;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
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
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.sign.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignActionPSD extends SignAction {

public static HashMap<Block, List<Sign>> psdSignCache = new HashMap<>();
public static boolean updateSignCache = false;

public static void updatePSD () {updateSignCache = true;}

@Override
public boolean match (SignActionEvent info) {
		return info.isType("psd", "platformdoor", "traindoor", "door");
	}

@Override
public void execute (SignActionEvent info) {
	if (!info.isAction(SignActionType.REDSTONE_CHANGE, SignActionType.GROUP_ENTER, SignActionType.GROUP_LEAVE)) return;

	// Parse scan size
	int dx = 1; int dy = 2; int dz = 1; String coords = info.getLine(1); int firstSpace = coords.indexOf(' ');
	if (firstSpace != -1) {
		coords = coords.substring(firstSpace).trim(); if (!coords.isEmpty()) {
			String[] parts = coords.split("/"); if (parts.length >= 3) {
				dx = ParseUtil.parseInt(parts[0], dx); dy = ParseUtil.parseInt(parts[1], dy);
				dz = ParseUtil.parseInt(parts[2], dz);
			} else if (parts.length == 2) {
				dx = dz = ParseUtil.parseInt(parts[0], dx); dy = ParseUtil.parseInt(parts[1], dy);
			} else if (parts.length == 1) {
				dx = dy = dz = ParseUtil.parseInt(parts[0], dx);
			}
		}
	}

	// Get current block, and scan from there
	Block b = info.getAttachedBlock(); List<Sign> signs = getSigns(b, dx, dy, dz);

	MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger().info("Found " + signs.size() + " signs.");
	MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger().info("Action: " + info.getAction().toString());

	if (info.isAction(SignActionType.REDSTONE_CHANGE) && info.isPowered() && info.hasRailedMember()) {
		for (Sign sign : signs)
			executePSDSign(sign, true);
	} else if (info.isAction(SignActionType.REDSTONE_CHANGE) && !info.isPowered() && info.hasRailedMember()) {
		for (Sign sign : signs)
			executePSDSign(sign, false);
	}

}

@Override
public boolean build (SignChangeActionEvent event) {
		return SignBuildOptions.create().setName("train door and platform door opener").setDescription("automatically open train and platform doors").handle(event.getPlayer());
	}

private List<Sign> getSigns (Block block, int dx, int dy, int dz) {
	List<Sign> signs = new ArrayList<>();

	if (updateSignCache || psdSignCache.get(block) == null || psdSignCache.get(block).isEmpty()) {
		MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger().info("no psd found, searching...");
		int cx = block.getLocation().getBlockX(); int cy = block.getLocation().getBlockY();
		int cz = block.getLocation().getBlockZ(); for (int x = cx - dx; x <= cx + dx; ++x)
			for (int y = cy - dy; y <= cy + dy; ++y)
				for (int z = cz - dz; z <= cz + dz; ++z) {
					if (new Location(block.getLocation().getWorld(), x, y, z).getBlock().getState() instanceof Sign sign) {
						List<String> lines = AddonsUtil.parseComponents(sign.getSide(Side.FRONT).lines());
						if (lines.get(0).equalsIgnoreCase("[psd]")) signs.add(sign);
					}
				} psdSignCache.put(block, signs); return signs;
	}

	// psdSignCache#get(block) is NOT empty
	signs = psdSignCache.get(block);

	// check if the signs are correct
	for (Sign sign : signs) {
		List<String> lines = AddonsUtil.parseComponents(sign.getSide(Side.FRONT).lines());
		if (!(lines.get(0).equalsIgnoreCase("[psd]"))) {
			// goto front and return
			return getSigns(block, dx, dy, dz);
		}
	}

	return signs;
}

private void executePSDSign (Sign sign, boolean open) {
	List<String> lines = AddonsUtil.parseComponents(sign.getSide(Side.FRONT).lines());
	if (!lines.get(0).equalsIgnoreCase("[psd]")) return;

	var l = AddonsUtil.getReferenceBlock(sign).getLocation(); int x = l.getBlockX(); int y = l.getBlockY();
	int z = l.getBlockZ(); var w = l.getWorld();

	if (open) {
		long openTicks = AddonsUtil.parseTicks(lines.get(2));
		var dir = Direction.parse(lines.get(3)).getDirection(AddonsUtil.getSignFacing(sign));

		for (int i = 1; i <= 5; i++) {
			var location = new Location(w, x, y + i, z);
			// open for 5 minutes if no time is set
			AddonsUtil.openDoor(location, dir, openTicks > 0 ? openTicks : 6000);

			// TODO: if openticks > 0 but the door is closed before the timer ends, DO NOT continue with flashLamp(location, openTicks)!!!
			if (openTicks > 0) {flashLamp(location, openTicks);} else flashLamp(location, true);
		}
	} else {
			for (int i = 1; i <= 5; i++) {
				MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger().info("closing");
				var location = new Location(w, x, y + i, z); location.add(0d, 1d, 0d); AddonsUtil.closeDoor(location);
				flashLamp(location, false);
			}
	}
}

private void flashLamp (Location location, boolean endOn) {
	Block block = location.getBlock(); if (block.getState().getBlockData() instanceof Lightable lightable) {
		var flashTask = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			lightable.setLit(!lightable.isLit()); block.setBlockData(lightable);
		}, 0, 5); Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			flashTask.cancel(); lightable.setLit(endOn); block.setBlockData(lightable);
		}, 45);
		}
	}

private void flashLamp (Location location, long openTicks) {
	Block block = location.getBlock(); if (block.getState().getBlockData() instanceof Lightable lightable) {
		Runnable flashRun = () -> {
			lightable.setLit(!lightable.isLit()); block.setBlockData(lightable);
		};

		// opening
		var open = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), flashRun, 0, 5);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			open.cancel(); lightable.setLit(true); block.setBlockData(lightable);
		}, 45);

		// closing
		var close = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), flashRun, openTicks, 5);
		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
			close.cancel(); lightable.setLit(false); block.setBlockData(lightable);
		}, openTicks + 90);
	}
}
}
