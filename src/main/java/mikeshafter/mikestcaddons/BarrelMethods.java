package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.intellij.lang.annotations.Subst;


public record BarrelMethods(MinecartGroup group) {
  
  public void announce(String message) {
    for (MinecartMember<?> member : group) {
      announce(member, message);
    }
  }
  
  public void announce(MinecartMember<?> member, String message) {
    member.getEntity().getPlayerPassengers().forEach(player -> TrainCarts.sendMessage(player, message));
  }
  
  public void setVariable(String identifier, String value) {
    Variables.get(identifier).set(value);
  }
  
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
  
  public void playSound(String sound, String source, float volume, float pitch) {
    group.forEach(member -> playSound(member, sound, source, volume, pitch));
  }
  
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
