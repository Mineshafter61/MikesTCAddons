package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;


public class BarrelUtil {
  
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
    for (MinecartMember<?> member : group) {
      _announce(member, message);
    }
  }
  
  // Helper method for above method
  public static void _announce(MinecartMember<?> member, String message) {
    member.getEntity().getPlayerPassengers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
  }
  
  // Sends an announcement in a cuboid area
  public static void announceCuboid(double x1, double y1, double z1, double x2, double y2, double z2, String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Location location = player.getLocation();
      if (Math.abs(x1-location.getX()) < Math.abs(x1-x2) && Math.abs(y1-location.getY()) < Math.abs(y1-y2) && Math.abs(z1-location.getZ()) < Math.abs(z1-z2))
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
    group.forEach(member -> playSound(member, sound, source, volume, pitch));
  }
  
  // Helper method for above method
  public static void playSound(MinecartMember<?> member, @Subst("minecraft") String sound, String source, float volume, float pitch) {
//    Sound.Source s = switch (source) {
//      case "music" -> Sound.Source.MUSIC;
//      case "record" -> Sound.Source.RECORD;
//      case "weather" -> Sound.Source.WEATHER;
//      case "block" -> Sound.Source.BLOCK;
//      case "hostile" -> Sound.Source.HOSTILE;
//      case "neutral" -> Sound.Source.NEUTRAL;
//      case "player" -> Sound.Source.PLAYER;
//      case "ambient" -> Sound.Source.AMBIENT;
//      case "voice" -> Sound.Source.VOICE;
//      default -> Sound.Source.MASTER;
//    };
    member.getEntity().getPlayerPassengers().forEach(player -> _playSound(player, sound, source, volume, pitch));
  }
  
  // Helper method for all playSound methods
  public static void _playSound(Player player, @Subst("minecraft") String sound, String source, float volume, float pitch) {
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
  
}
