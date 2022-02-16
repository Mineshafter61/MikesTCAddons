package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.ChatColor;
import org.intellij.lang.annotations.Subst;


public record BarrelMethods(MinecartGroup group) {
  
  // Announce to all players in a train
  public void announce(String message) {
    for (MinecartMember<?> member : group) {
      announce(member, message);
    }
  }
  
  // Helper method for above method
  public void announce(MinecartMember<?> member, String message) {
    member.getEntity().getPlayerPassengers().forEach(player -> TrainCarts.sendMessage(player, ChatColor.translateAlternateColorCodes('&', message)));
  }
  
  // Sets a certain SignLink variable
  public void setVariable(String identifier, String value) {
    Variables.get(identifier).set(ChatColor.translateAlternateColorCodes('&', value));
  }
  
  // Sets the ticker method for a SignLink variable
  public void setTicker(String identifier, String tickMode, long interval) {
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
  public void playSound(String sound, String source, float volume, float pitch) {
    group.forEach(member -> playSound(member, sound, source, volume, pitch));
  }
  
  // Helper method for above method
  public void playSound(MinecartMember<?> member, @Subst("minecraft") String sound, String source, float volume, float pitch) {
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
    member.getEntity().getPlayerPassengers().forEach(player -> player.playSound(Sound.sound(Key.key(sound), s, volume, pitch)));
  }
  
}
