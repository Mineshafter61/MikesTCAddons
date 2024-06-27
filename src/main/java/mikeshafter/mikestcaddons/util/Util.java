package mikeshafter.mikestcaddons.util;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import mikeshafter.mikestcaddons.dynamics.PlatformGate;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;

import java.util.ArrayList;
import java.util.List;


public class Util {

public static List<PlatformGate> gates = new ArrayList<>(); // List for forced closing

// Parse a string to ticks
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
        if (
            ((x1 <= x && x <= x2) || (x1 >= x && x >= x2)) &&
            ((y1 <= y && y <= y2) || (y1 >= y && y >= y2)) &&
            ((z1 <= z && z <= z2) || (z1 >= z && z >= z2))
        ) player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}

// Sends an announcement in a spherical area
public static void announceSphere(double x, double y, double z, double r, String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        if ((location.getX()-x)*(location.getX()-x)+(location.getY()-y)*(location.getY()-y)+(location.getZ()-z)*(location.getZ()-z) <= r*r)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}

public static void announcePoly (String message, int[]... points) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        if (inPolygonWE(x, y, z, points))
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
        if (Math.abs(x1-location.getX()) < Math.abs(x1-x2) && Math.abs(y1-location.getY()) < Math.abs(y1-y2) && Math.abs(z1-location.getZ()) < Math.abs(z1-z2))
            _playSound(player, sound, source, volume, pitch);
    }
}

// Play sound in a sphere shape
public static void playSoundSphere(double x, double y, double z, double r, String sound, String source, float volume, float pitch) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        if ((location.getX()-x)*(location.getX()-x)+(location.getY()-y)*(location.getY()-y)+(location.getZ()-z)*(location.getZ()-z) <= r*r)
            _playSound(player, sound, source, volume, pitch);
    }
}

public static void playSoundPoly (String sound, String source, float volume, float pitch, int[]... points) {
    for (Player player : Bukkit.getOnlinePlayers()) {
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        if (inPolygonWE(x, y, z, points))
            _playSound(player, sound, source, volume, pitch);
    }
}
// Open door smoothly
public static void openDoor(World world, int x, int y, int z, BlockFace direction, long openTime) {
    Location location = new Location(world, x, y, z);
    Block block = location.getBlock();
    // optimise code
    if (!(block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR || block.getType() == Material.VOID_AIR) && world.getNearbyEntities(location, 48, 32, 48, (entity) -> entity.getType() == EntityType.PLAYER).size() > 0) {
        PlatformGate platformGate = new PlatformGate(block, direction, openTime);
        platformGate.activateGate();
    }
}

public static void closeDoor(World world, int x, int y, int z) {
    for (PlatformGate gate : gates) {
        if (gate.getBlock().getLocation().equals(new Location(world, x, y, z))) gate.closeGate(false);
    }
}

}
