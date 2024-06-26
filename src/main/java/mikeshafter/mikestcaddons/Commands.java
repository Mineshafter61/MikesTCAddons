package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.cloud.CloudSimpleHandler;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import mikeshafter.mikestcaddons.attachments.Changer;
import mikeshafter.mikestcaddons.attachments.Swapper;
import org.bukkit.Material;
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

	public CloudSimpleHandler getHandler() {
		return cloud;
	}

	public void enable(MikesTCAddons plugin) {
		cloud.enable(plugin);
		cloud.annotations(this);
		cloud.helpCommand(Collections.singletonList("mikestcaddons"), "Shows information about all of Mike's TC Addons' commands");
}


	@Command("throttle <type>")
	@CommandDescription("Turns on or off throttle")
	@Permission("mikestcaddons.throttle")
	public void throttleCmd(
			final CommandSender sender,
			final MikesTCAddons plugin,
			final @Argument("type") String throttleType
	) {
		// Command method here
}

	@Command("swap <animation0> <animation1>")
	@CommandDescription("Swaps two animations")
	@Permission("mikestcaddons.swap")
	public void swapCmd(
			final CommandSender sender,
			final MikesTCAddons plugin,
			final @Argument("animation0") String animation0,
			final @Argument("animation1") String animation1
	) {
		if (!(sender instanceof Player player)) return;
		// Get vehicle
		MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
		// Fail operation under these conditions
		if (vehicle == null || !vehicle.getProperties().hasOwnership(player)) return;

		for (MinecartMember<?> member : vehicle) {
			Swapper a = new Swapper(member, animation0, animation1);
			a.run();
		}
	}

	@Command("changeitem <name> <item_type> <custom_model_data>")
	@CommandDescription("Changes an item on the train to the item specified")
	@Permission("mikestcaddons.swap")
	public void changeItemCmd(
			final CommandSender sender,
			final MikesTCAddons plugin,
			final @Argument("name") String name,
			final @Argument("item_type") String material,
			final @Argument("custom_model_data") Integer customModelData
	) {
		if (!(sender instanceof Player player)) return;
		// Get vehicle
		MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
		// Fail operation under these conditions
		if (vehicle == null || !vehicle.getProperties().hasOwnership(player)) return;

		for (MinecartMember<?> member : vehicle) {
			Changer a = new Changer(member, name, Material.getMaterial(material.toUpperCase()), customModelData);
			a.run();
		}
}

	@Command("decouple <number>")
	@CommandDescription("Decouples carts")
	@Permission("mikestcaddons.throttle")
	public void decoupleCmd(
			final CommandSender sender,
			final MikesTCAddons plugin,
			final @Argument("number") Integer n
	) {
		if (!(sender instanceof Player player)) return;
		// Get vehicle
		MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
		// Fail operation under these conditions
		int number = n;
		if (number == 0 || vehicle == null || !vehicle.getProperties().hasOwnership(player)) return;

		// Make the train into a list for easier editing
		List<MinecartMember<?>> members = vehicle.stream().toList();
		TrainProperties properties = vehicle.getProperties();
		int size = members.size();
		// New train from existing
		MinecartMember<?>[] newGroup = new MinecartMember<?>[Math.abs(number)];

		// if the number to decouple is negative, decouple from the rear:
		if (number < 0) {
			number = -number;

			// Remove carts sequentially
			for (int i = size - 1, j = 0; i > size - number; i--, j++) {
				newGroup[j] = members.get(i);
				vehicle.remove(i);
			}
		}

		// else decouple from the front
		else {
			// Remove carts sequentially
			for (int i = 0; i < number; i++) newGroup[i] = members.get(i);
			vehicle.subList(0, number).clear();
		}

		// Create new train and store
		MinecartGroupStore.createSplitFrom(properties, newGroup);

}
}
