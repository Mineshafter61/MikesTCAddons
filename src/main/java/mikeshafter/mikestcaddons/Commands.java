package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import mikeshafter.mikestcaddons.attachments.Changer;
import mikeshafter.mikestcaddons.attachments.Swapper;
import mikeshafter.mikestcaddons.util.AddonsUtil;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.processing.CommandContainer;

import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
@CommandContainer
public class Commands {

private final CloudSimpleHandler cloud = new CloudSimpleHandler();

public CloudSimpleHandler getHandler () {return cloud;}

public void enable (MikesTCAddons plugin) {
	cloud.enable(plugin);
	cloud.annotations(this);
	cloud.helpCommand(Collections.singletonList("mikestcaddons"), "Shows information about all of Mike's TC Addons' commands");
}

@Command("throttle <type>")
@CommandDescription("Turns on or off throttle")
@Permission("mikestcaddons.throttle")
public void throttleCmd (final CommandSender sender, final MikesTCAddons plugin, final @Argument("type") String throttleType) {
	// Command method here
}

@Command("swap <animation0> <animation1>")
@CommandDescription("Swaps two animations")
@Permission("mikestcaddons.swap")
public void swapCmd (final CommandSender sender, final MikesTCAddons plugin, final @Argument("animation0") String animation0, final @Argument("animation1") String animation1) {
	if (!(sender instanceof Player player)) return;
	MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
	if (vehicle == null || !vehicle.getProperties().hasOwnership(player)) return;

	for (MinecartMember<?> member : vehicle) {
		Swapper a = new Swapper(member, animation0, animation1);
		a.run();
	}
}

@Command("changeitem <name> <item_type> <custom_model_data>")
@CommandDescription("Changes an item on the train to the item specified")
@Permission("mikestcaddons.changeitem")
public void changeItemCmd (final CommandSender sender, final MikesTCAddons plugin, final @Argument("name") String name, final @Argument("item_type") String material, final @Argument("custom_model_data") Integer customModelData) {
	if (!(sender instanceof Player player)) return;
	MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
	if (vehicle == null || !vehicle.getProperties().hasOwnership(player)) return;

	for (MinecartMember<?> member : vehicle) {
		Changer a = new Changer(member, name, Material.getMaterial(material.toUpperCase()), customModelData);
		a.run();
	}
}

@Command("decouple <number>")
@CommandDescription("Decouples carts")
@Permission("mikestcaddons.decouple")
public void decoupleCmd (final CommandSender sender, final MikesTCAddons plugin, final @Argument("number") Integer n) {
	if (!(sender instanceof Player player)) return;
	MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
	int number = n;
	if (number == 0 || vehicle == null || !vehicle.getProperties().hasOwnership(player)) return;

	// Make the train into a list for easier editing
	List<MinecartMember<?>> members = vehicle.stream().toList();
	TrainProperties properties = vehicle.getProperties();
	int size = members.size();
	MinecartMember<?>[] newGroup = new MinecartMember<?>[Math.abs(number)];

	// if the number to decouple is negative, decouple from the rear:
	if (number < 0) {
		number = -number;

		for (int i = size - 1, j = 0; i > size - number; i--, j++) {
			newGroup[j] = members.get(i);
			vehicle.remove(i);
		}
	}

	else {
		// decouple from the front
		for (int i = 0; i < number; i++) newGroup[i] = members.get(i);
		vehicle.subList(0, number).clear();
	}

	MinecartGroupStore.createSplitFrom(properties, newGroup);
}

@Command("opengate <x> <y> <z> <direction> <time>")
@CommandDescription("Opens glass doors")
@Permission("mikestcaddons.gate")
public void gateCmd (final CommandSender sender, final MikesTCAddons plugin, final @Argument("x") String x, final @Argument("y") String y, final @Argument("z") String z, final @Argument("direction") String direction, final @Argument("time") String time) {
	long ticks = AddonsUtil.parseTicks(time);
	World world;
	int X, Y, Z;
	if (sender instanceof Player player) {
		world = player.getWorld();
		X = AddonsUtil.parseRelative(x, 'x', player.getLocation());
		Y = AddonsUtil.parseRelative(y, 'y', player.getLocation());
		Z = AddonsUtil.parseRelative(z, 'z', player.getLocation());
	}
	else {
		BlockCommandSender commandBlock = (BlockCommandSender) sender;
		world = commandBlock.getBlock().getWorld();
		X = AddonsUtil.parseRelative(x, 'x', commandBlock.getBlock().getLocation());
		Y = AddonsUtil.parseRelative(y, 'y', commandBlock.getBlock().getLocation());
		Z = AddonsUtil.parseRelative(z, 'z', commandBlock.getBlock().getLocation());
	}
	BlockFace dir = switch (direction.toUpperCase()) {
		case "S", "SOUTH" -> BlockFace.SOUTH;
		case "N", "NORTH" -> BlockFace.NORTH;
		case "E", "EAST" -> BlockFace.EAST;
		case "W", "WEST" -> BlockFace.WEST;
		default -> BlockFace.SELF;
	};
	AddonsUtil.openDoor(world, X, Y, Z, dir, ticks);
}

@Command("closegate <x> <y> <z>")
@CommandDescription("Closes glass doors")
@Permission("mikestcaddons.gate")
public void gateCmd (final CommandSender sender, final MikesTCAddons plugin, final @Argument("x") String x, final @Argument("y") String y, final @Argument("z") String z) {
	World world;
	int X, Y, Z;
	if (sender instanceof Player player) {
		world = player.getWorld();
		X = AddonsUtil.parseRelative(x, 'x', player.getLocation());
		Y = AddonsUtil.parseRelative(y, 'y', player.getLocation());
		Z = AddonsUtil.parseRelative(z, 'z', player.getLocation());
	}
	else {
		BlockCommandSender commandBlock = (BlockCommandSender) sender;
		world = commandBlock.getBlock().getWorld();
		X = AddonsUtil.parseRelative(x, 'x', commandBlock.getBlock().getLocation());
		Y = AddonsUtil.parseRelative(y, 'y', commandBlock.getBlock().getLocation());
		Z = AddonsUtil.parseRelative(z, 'z', commandBlock.getBlock().getLocation());
	} AddonsUtil.closeDoor(world, X, Y, Z);
}

}
