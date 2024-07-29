package mikeshafter.mikestcaddons.dynamics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
//import com.bergerkiller.bukkit.tc.Direction;
//import com.bergerkiller.bukkit.tc.PowerState;
//import mikeshafter.mikestcaddons.MikesTCAddons;
//import mikeshafter.mikestcaddons.util.AddonsUtil;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.block.Sign;
//import org.bukkit.block.data.Lightable;
//import org.bukkit.block.data.type.WallSign;
//import org.bukkit.block.sign.Side;
//import org.bukkit.event.block.BlockPhysicsEvent;
//import java.util.List;

public class PSDListener implements Listener {

@EventHandler
public void onSignChange (SignChangeEvent event) {
	String t = parseComponent(event.line(0));
	if (t.equalsIgnoreCase("[psd]")) {
		event.getPlayer().sendMessage(Component.text("You have created a PSD sign!"));
		SignActionPSD.updatePSD();
	}
}

private String parseComponent (final Component c) {
	if (c instanceof TextComponent) {return ((TextComponent) c).content();} else if (c == null) {return "";} else return c.examinableName();
}

//@EventHandler
//public void onBlockPhysics (BlockPhysicsEvent event) {
//	if (!(event.getBlock().getBlockData() instanceof WallSign && event.getBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign && event.getBlock().getState() instanceof Sign sign)) {
//		return;
//	}
//
//	List<String> lines = AddonsUtil.parseComponents(sign.getSide(Side.FRONT).lines());
//	if (!lines.get(0).equalsIgnoreCase("[psd]")) return;
//
//	boolean powerState = PowerState.isSignPowered(event.getBlock());
//	var loc = AddonsUtil.getReferenceBlock(sign).getLocation();
//
//	if (powerState) {
//		long openTicks = AddonsUtil.parseTicks(lines.get(2));
//		var dir = Direction.parse(lines.get(3)).getDirection(AddonsUtil.getSignFacing(sign));
//		for (int i = 1; i <= 5; i++) {
//			loc.add(0d, 1d, 0d);
//			// open for 5 minutes if no time is set
//			AddonsUtil.openDoor(loc, dir, openTicks > 0 ? openTicks : 6000);
//			if (openTicks > 0) {flashLamp(loc, openTicks);} else flashLamp(loc, true);
//		}
//	} else {
//		for (int i = 1; i <= 5; i++) {
//			loc.add(0d, 1d, 0d); AddonsUtil.closeDoor(loc); flashLamp(loc, false);
//		}
//	}
//}
//
//private void flashLamp (Location location, long openTicks) {
//	if (location.getBlock().getState().getBlockData() instanceof Lightable lightable) {
//		Runnable flashRun = () -> lightable.setLit(!lightable.isLit());
//
//		// opening
//		var open = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), flashRun, 0, 5);
//		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
//			open.cancel(); lightable.setLit(true);
//		}, 45);
//
//		// closing
//		var close = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), flashRun, openTicks, 5);
//		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
//			close.cancel(); lightable.setLit(false);
//		}, openTicks + 45);
//		}
//	}
//
//private void flashLamp (Location location, boolean endOn) {
//	if (location.getBlock().getState().getBlockData() instanceof Lightable lightable) {
//		var flashTask = Bukkit.getScheduler().runTaskTimer(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> lightable.setLit(!lightable.isLit()), 0, 5);
//		Bukkit.getScheduler().runTaskLater(MikesTCAddons.getPlugin(MikesTCAddons.class), () -> {
//			flashTask.cancel(); lightable.setLit(endOn);
//		}, 45);
//	}
//}

}
