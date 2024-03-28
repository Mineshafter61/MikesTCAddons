package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import mikeshafter.mikestcaddons.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static mikeshafter.mikestcaddons.util.Util.swapMember;


public class Commands implements TabExecutor {
@Override public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
    // throttle
    if (command.getName().equalsIgnoreCase("throttle") && sender instanceof Player player && args.length >= 1 && sender.hasPermission("mikestcaddons.throttle")) {
        if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null) {
            MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
            if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
                if (args[0].equalsIgnoreCase("mu")) {
                    ThrottleManager.addThrottle(player, vehicle.size());
                    return true;
                }
                else if (args[0].equalsIgnoreCase("loc")) {
                    if (args.length == 2 && args[1].matches("\\d+"))
                        ThrottleManager.addThrottle(player, Integer.parseInt(args[1]));
                    else ThrottleManager.addThrottle(player, 1);
                    return true;
                }
                else if (args[0].equalsIgnoreCase("off")) {
                    ThrottleManager.removeThrottle(player);
                    return true;
                }
            }
            else {
                player.sendMessage(ChatColor.RED + "Please claim the train first!");
                return true;
            }
        }
    }

    // door
    else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player player && args.length == 2 && sender.hasPermission("mikestcaddons.door")) {
        return door(player, args);
    }

    // swap
    else if (command.getName().equalsIgnoreCase("swap") && sender instanceof Player player && args.length == 0 && sender.hasPermission("mikestcaddons.swap")) {
        return swap(player);
    }

    // decouple
    else if (command.getName().equalsIgnoreCase("decouple") && sender instanceof Player player && args.length == 1 && sender.hasPermission("mikestcaddons.decouple")) {
        int d = Integer.parseInt(args[0]);  // number of carts to decouple
        return decouple(player, d);
    }

    // opengate
    else if (command.getName().equalsIgnoreCase("opengate") && (sender instanceof Player || sender instanceof BlockCommandSender) && args.length == 5 && sender.hasPermission("mikestcaddons.gate")) {
        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        }
        else {
            BlockCommandSender commandBlock = (BlockCommandSender) sender;
            world = commandBlock.getBlock().getWorld();
        }
        int x = getInteger(args[0], 'x', sender);
        int y = getInteger(args[1], 'y', sender);
        int z = getInteger(args[2], 'z', sender);
        BlockFace direction = switch (args[3].toUpperCase()) {
            case "S", "SOUTH" -> BlockFace.SOUTH;
            case "N", "NORTH" -> BlockFace.NORTH;
            case "E", "EAST" -> BlockFace.EAST;
            case "W", "WEST" -> BlockFace.WEST;
            default -> BlockFace.SELF;
        };
        if (direction == BlockFace.SELF) return false;
        int openTime = Integer.parseInt(args[4]);
        Util.openDoor(world, x, y, z, direction, openTime);
        return true;
    }


    // closegate
    else if (command.getName().equalsIgnoreCase("closegate") && (sender instanceof Player || sender instanceof BlockCommandSender) && args.length == 3 && sender.hasPermission("mikestcaddons.gate")) {
        World world;
        if (sender instanceof Player player) {
            world = player.getWorld();
        }
        else {
            BlockCommandSender commandBlock = (BlockCommandSender) sender;
            world = commandBlock.getBlock().getWorld();
        }
        int x = getInteger(args[0], 'x', sender);
        int y = getInteger(args[1], 'y', sender);
        int z = getInteger(args[2], 'z', sender);

        Util.closeDoor(world, x, y, z);
        return true;
    }

    // reload
    else if (command.getName().equalsIgnoreCase("reload") && sender.hasPermission("mikestcaddons.reload")) {
        MikesTCAddons.getPlugin(MikesTCAddons.class).reloadConfig();
        return true;
    }
    return false;
}
@Override public List<String> onTabComplete(@NotNull CommandSender sender,  Command command, @NotNull String alias,  String[] args) {
    List<String> completions = new ArrayList<>();
    List<String> commands = new ArrayList<>();

    // throttle
    if (command.getName().equalsIgnoreCase("throttle") && sender.hasPermission("mikestcaddons.throttle")) {
        if (args.length == 1) {
            commands.add("mu");
            commands.add("loc");
            commands.add("off");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
    } else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player && sender.hasPermission("mikestcaddons.door")) {
        if (args.length == 1) {
            commands.add("l");
            commands.add("r");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        } else if (args.length == 2) {
            commands.add("o");
            commands.add("c");
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
    } else if (command.getName().equalsIgnoreCase("opengate") && (sender instanceof Player player)) {
        if (args.length == 1) {
            commands.add(String.valueOf(Objects.requireNonNull(player.getTargetBlock(5)).getX()));
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if (args.length == 2) {
            commands.add(String.valueOf(Objects.requireNonNull(player.getTargetBlock(5)).getY()));
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if (args.length == 3) {
            commands.add(String.valueOf(Objects.requireNonNull(player.getTargetBlock(5)).getZ()));
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }
        if (args.length == 4) {
            commands.add("NORTH");
            commands.add("SOUTH");
            commands.add("EAST");
            commands.add("WEST");
            StringUtil.copyPartialMatches(args[3], commands, completions);
        }
    } else if (command.getName().equalsIgnoreCase("closegate") && (sender instanceof Player player)) {
        if (args.length == 1) {
            commands.add(String.valueOf(Objects.requireNonNull(player.getTargetBlock(5)).getX()));
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if (args.length == 2) {
            commands.add(String.valueOf(Objects.requireNonNull(player.getTargetBlock(5)).getY()));
            StringUtil.copyPartialMatches(args[1], commands, completions);
        }
        if (args.length == 3) {
            commands.add(String.valueOf(Objects.requireNonNull(player.getTargetBlock(5)).getZ()));
            StringUtil.copyPartialMatches(args[2], commands, completions);
        }
    }

    return completions;
}


private boolean door (Player player, String[] args) {
    // Get the train the player is editing
    if (CartPropertiesStore.getEditing(player) != null) {
        MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();

        // Check if the player is an owner
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {

            // left side
            if (args[0].contains("l")) {
                AnimationOptions options = new AnimationOptions();

                // c for close, o for open
                if (args[1].contains("c")) {
                    options.setSpeed(-1);
                }
                else {
                    options.setSpeed(1);
                }

                options.setName("door_L");
                vehicle.playNamedAnimation(options);
                return true;

                // right side
            }
            else if (args[0].contains("r")) {
                AnimationOptions options = new AnimationOptions();

                // c for close, o for open
                if (args[1].contains("c")) {
                    options.setSpeed(-1);
                }
                else {
                    options.setSpeed(1);
                }

                options.setName("door_R");
                vehicle.playNamedAnimation(options);
                return true;
            }
        }
    }
    return false;
}


public boolean swap (Player player) {
    if (CartPropertiesStore.getEditing(player).getHolder().getGroup() != null) {
        MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
        if (vehicle.getProperties().hasOwnership(player)) {
            for (MinecartMember<?> member : vehicle) swapMember(member);
            player.sendMessage("Swapped left and right doors.");
        }
    }
    else player.sendMessage("You need to own the train first!");
    return true;
}


public boolean decouple (Player player, int toDecouple) {
    // Get the train the player is editing
    if (CartPropertiesStore.getEditing(player).getHolder().getGroup() != null) {
        MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();

        // Check if the player is an owner
        if (vehicle.getProperties().hasOwnership(player)) {

            List<MinecartMember<?>> members = vehicle.stream().toList();  // Make the train into a list for easier editing
            TrainProperties properties = vehicle.getProperties();  // Get properties
            int size = members.size();  // Get size
            MinecartMember<?>[] newGroup = new MinecartMember<?>[Math.abs(toDecouple)];  // New train from existing

            // if the number to decouple is negative, decouple from the rear:
            if (toDecouple < 0) {
                toDecouple = -toDecouple;

                // Remove carts sequentially
                int j = 0;
                for (int i = size - 1; i > size - toDecouple; i--) {
                    newGroup[j] = members.get(i);
                    ++j;
                    vehicle.remove(i);
                }

                // Create new train and store
                MinecartGroupStore.createSplitFrom(properties, newGroup);
                return true;
            }

            // else decouple from the front
            else if (toDecouple > 0) {
                // Remove carts sequentially
                for (int i = 0; i < toDecouple; i++) newGroup[i] = members.get(i);
                vehicle.subList(0, toDecouple).clear();

                // Create new train and store
                MinecartGroupStore.createSplitFrom(properties, newGroup);
                return true;
            }
        }
    }
    return false;
}


private int getInteger(String str, char axis, CommandSender sender) {
    if ((sender instanceof Player || sender instanceof BlockCommandSender) && str.startsWith("~")) {
        Location loc = sender instanceof Player ? ((Player) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
        int i = str.substring(1).isEmpty() ? 0 : Integer.parseInt(str.substring(1));
        switch (axis) {
            case 'x' -> i += loc.getBlockX();
            case 'y' -> i += loc.getBlockY();
            case 'z' -> i += loc.getBlockZ();
        }

        return i;
    } else return Integer.parseInt(str);
}
}
