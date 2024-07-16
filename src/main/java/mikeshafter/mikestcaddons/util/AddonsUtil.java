package mikeshafter.mikestcaddons.util;

import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import mikeshafter.mikestcaddons.MikesTCAddons;
import mikeshafter.mikestcaddons.dynamics.PlatformGate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;

import java.util.*;


public class AddonsUtil {

public static Map<Location, PlatformGate> gates = Collections.synchronizedMap(new HashMap<>());

/**
 * Parse a string to ticks
 *
 * @param timestring String containing the time to parse
 * @return Number of ticks
 */
public static long parseTicks(String timestring) {
    long rval = 0;
    if (!LogicUtil.nullOrEmpty(timestring)) {
        String[] parts = timestring.split(":");
        if (parts.length == 1) {
            //Seconds display only
            rval = (long) (ParseUtil.parseDouble(parts[0], 0.0)*20);
        } else if (parts.length == 2) {
            //Min:Sec
            rval = ParseUtil.parseLong(parts[0], 0)*1200;
            rval += ParseUtil.parseLong(parts[1], 0)*20;
        } else if (parts.length == 3) {
            //Hour:Min:Sec
            rval = ParseUtil.parseLong(parts[0], 0)*72000;
            rval += ParseUtil.parseLong(parts[1], 0)*1200;
            rval += ParseUtil.parseLong(parts[2], 0)*20;
        }
    }
    return rval;
}

// Announce to all players in a train
public static void announce(MinecartGroup group, String message) {
    for (MinecartMember<?> member : group) member.getEntity().getPlayerPassengers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
}

// Sends an announcement in a cuboid area
public static void announceCuboid(int x1, int y1, int z1, int x2, int y2, int z2, String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        if (((x1 <= x && x <= x2) || (x1 >= x && x >= x2)) && ((y1 <= y && y <= y2) || (y1 >= y && y >= y2)) && ((z1 <= z && z <= z2) || (z1 >= z && z >= z2))) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}

// Sends an announcement in a spherical area
public static void announceSphere(double x, double y, double z, double r, String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        if ((location.getX() - x) * (location.getX() - x) + (location.getY() - y) * (location.getY() - y) + (location.getZ() - z) * (location.getZ() - z) <= r * r) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}

public static void announcePoly (String message, int[]... points) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        if (inPolygonWE(x, y, z, points)) {player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}
    }
}

private static boolean inPolygonWE (int x, int y, int z, int[][] vertices) {
    boolean result = java.util.stream.IntStream.range(0, vertices.length)
        .map(i -> 
            (vertices[i][2] > z) != (vertices[(i + 1) % vertices.length][2] > z) ?
                (x < (vertices[(i + 1) % vertices.length][0] - vertices[i][0]) * (z - vertices[i][2]) / (vertices[(i + 1) % vertices.length][2] - vertices[i][2]) + vertices[i][0]) ?
                    1 : 0 
                : 0).sum() % 2 == 1;

    int minY = vertices[0][1], maxY = vertices[0][1];
    for (int j = 1; j < vertices.length; j++) {
        minY = Math.min(minY, vertices[j][1]);
        maxY = Math.max(maxY, vertices[j][1]);
    }
    return minY <= y && y <= maxY && result;
}

// Sets a certain SignLink variable
public static void setVariable(String identifier, String value) {
    Variables.get(identifier).set(ChatColor.translateAlternateColorCodes('&', value));
}

// Sets the ticker method for a SignLink variable
public static void setTicker(String identifier, String tickMode, long interval) {
    TickMode mode = switch (tickMode) {
        case "left" -> TickMode.LEFT;
        case "blink" -> TickMode.BLINK;
        case "right" -> TickMode.RIGHT;
        default -> TickMode.NONE;
    };
    Variables.get(identifier).getTicker().setMode(mode);
    Variables.get(identifier).getTicker().setInterval(interval);
}

// Plays a sound to all players in a train
public static void playSound(MinecartGroup group, String sound, String source, float volume, float pitch) {
    for (MinecartMember<?> member : group) member.getEntity().getPlayerPassengers().forEach(player -> _playSound(player, sound, source, volume, pitch));
}

// Helper method for all playSound methods
private static void _playSound(Player player, @Subst("minecraft") String sound, String source, float volume, float pitch) {
    Sound.Source s = switch (source) {
        case "music" -> Sound.Source.MUSIC;
        case "record" -> Sound.Source.RECORD;
        case "weather" -> Sound.Source.WEATHER;
        case "block" -> Sound.Source.BLOCK;
        case "hostile" -> Sound.Source.HOSTILE;
        case "neutral" -> Sound.Source.NEUTRAL;
        case "player" -> Sound.Source.PLAYER;
        case "ambient" -> Sound.Source.AMBIENT;
        case "voice" -> Sound.Source.VOICE;
        default -> Sound.Source.MASTER;
    };

    player.playSound(Sound.sound(Key.key(sound), s, volume, pitch));
}

// Play sound in a cuboid shape
public static void playSoundCuboid(double x1, double y1, double z1, double x2, double y2, double z2, String sound, String source, float volume, float pitch) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        if (Math.abs(x1 - location.getX()) < Math.abs(x1 - x2) && Math.abs(y1 - location.getY()) < Math.abs(y1 - y2) && Math.abs(z1 - location.getZ()) < Math.abs(z1 - z2)) {
            _playSound(player, sound, source, volume, pitch);
        }
    }
}

// Play sound in a sphere shape
public static void playSoundSphere(double x, double y, double z, double r, String sound, String source, float volume, float pitch) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        if ((location.getX() - x) * (location.getX() - x) + (location.getY() - y) * (location.getY() - y) + (location.getZ() - z) * (location.getZ() - z) <= r * r) {
            _playSound(player, sound, source, volume, pitch);
        }
    }
}

public static void playSoundPoly (String sound, String source, float volume, float pitch, int[]... points) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        if (inPolygonWE(x, y, z, points)) {_playSound(player, sound, source, volume, pitch);}
    }
}

public static void openDoor(World world, int x, int y, int z, BlockFace direction, long openTime) {
    Location location = new Location(world, x, y, z);
    openDoor(location, direction, openTime);
}

    // Open door smoothly
public static void openDoor (Location loc, BlockFace direction, long openTime) {
    Block block = loc.getBlock();
    // optimise code
    if (!(block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR || block.getType() == Material.VOID_AIR) && (block.getBlockData() instanceof Fence || block.getBlockData() instanceof GlassPane) && !loc.getWorld().getNearbyEntities(loc, 48, 32, 48, (entity) -> entity.getType() == EntityType.PLAYER).isEmpty()) {
        PlatformGate platformGate = new PlatformGate(block, direction, openTime);
        platformGate.activateGate(); gates.put(loc, platformGate);
    }
}

public static void closeDoor (Location loc) {
    if (gates.get(loc) != null) {
        MikesTCAddons.getPlugin(MikesTCAddons.class).getLogger().info("Closing gate " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
        gates.get(loc).closeGate(false); gates.remove(loc);
    }
}

public static void closeDoor(World world, int x, int y, int z) {
    Location location = new Location(world, x, y, z);
    closeDoor(location);
}

    /** Turn relative coordinates into absolute coordinates
     * @param str Coordinate string
     * @param axis Axis to parse
     * @param loc Current player location
     * @return Absolute coordinates from relative coordinates
     */
    public static int parseRelative (String str, char axis, Location loc) {
        if (!str.startsWith("~")) return Integer.parseInt(str);

        int i = str.substring(1).isEmpty() ? 0 : Integer.parseInt(str.substring(1)); switch (axis) {
            case 'x' -> i += loc.getBlockX(); case 'y' -> i += loc.getBlockY(); case 'z' -> i += loc.getBlockZ();
        } return i;
    }

/**
 * Gets the block to reference when using a sign
 *
 * @param sign Sign to check
 * @return The block to reference
 */
public static Block getReferenceBlock (org.bukkit.block.Sign sign) {
    if (sign.getBlockData() instanceof org.bukkit.block.data.type.WallSign) {
        return BlockUtil.getAttachedBlock((Block) sign);
    } return sign.getBlock();
}

/**
 * Gets the way a sign is facing
 *
 * @param sign Sign to parse
 * @return Returns the rotation of a standing sign, and the facing of a wall sign.
 */
public static BlockFace getSignFacing (org.bukkit.block.Sign sign) {
    if (sign.getBlockData() instanceof org.bukkit.block.data.type.WallSign w) {
        return w.getFacing();
    } else if (sign.getBlockData() instanceof org.bukkit.block.data.type.Sign s) {return s.getRotation();} else {
        return BlockFace.SELF;
    }
}

/**
 * Converts a list of Components componentList to a list of Strings.
 *
 * @param cList The component to parse.
 * @return a list of Strings from the list of Components
 */
public static List<String> parseComponents (List<Component> cList) {
    List<String> r = new ArrayList<>(); cList.forEach(c -> r.add(parseComponent(c))); return r;
}

/**
 * Converts a Component c to a String. For the other way around, use {@link TextComponent#content(String)}.
 *
 * @param c The component to parse.
 * @return the Component in String format
 */
public static String parseComponent (final Component c) {
    if (c instanceof TextComponent) {return ((TextComponent) c).content();} else if (c == null) {return "";} else {
        return c.examinableName();
    }
}
}
