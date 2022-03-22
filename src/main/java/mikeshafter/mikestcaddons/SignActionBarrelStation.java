package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionStation;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.intellij.lang.annotations.Subst;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;


public class SignActionBarrelStation extends SignActionStation {
  
  @Override
  public boolean match(SignActionEvent event) {
    return event.isType("barrelsta", "specialsta");
  }
  
  @Override
  public void execute(SignActionEvent info) {
    Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
  
    // Same as station sign
    if (!info.isAction(SignActionType.REDSTONE_CHANGE, SignActionType.GROUP_ENTER, SignActionType.GROUP_LEAVE)) {
      return;
    }
    
    if (info.isAction(SignActionType.GROUP_ENTER)) {
  
      // Get group
      MinecartGroup group = info.getGroup();
  
      // Parse barrel
      BlockState state = info.getAttachedBlock().getState();
      if (state instanceof Container container) {
  
        // StringBuilder to build code
        StringBuilder builder = new StringBuilder();
  
        for (ItemStack itemStack : container.getInventory().getContents()) {
          if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof BookMeta bookMeta) {
  
            for (Component page : bookMeta.pages()) {
              TextComponent pageText = (TextComponent) page;
              // parse pages and append page
              builder.append(pageText.content());
            }
          }
        }
        // Code to be parsed
        String content = builder.toString();
  
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(content);
  
        if (data == null) return;
  
        // Top level allowed keys: A number indicating the number of seconds after the train has stopped at the station.
        // Negative numbers are not allowed.
        // Use parseTicks() to get the delay in a long.
        for (String key : data.keySet()) {
          long delay = parseTicks(key);
          if (data.get(key) instanceof Map dataMap) {
  
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
              for (Object o : dataMap.keySet()) {
  
                if (dataMap.get(o) instanceof Map params) {
    
                  String function = o.toString();
    
                  switch (function) {
                    case "announce" -> {  // 1 param only
                      String a = params.get("a").toString();
                      announce(group, a);
                    }
                    case "announce-cuboid" -> {
                      double x1 = Double.parseDouble(params.get("x1").toString());
                      double y1 = Double.parseDouble(params.get("y1").toString());
                      double z1 = Double.parseDouble(params.get("z1").toString());
                      double x2 = Double.parseDouble(params.get("x2").toString());
                      double y2 = Double.parseDouble(params.get("y2").toString());
                      double z2 = Double.parseDouble(params.get("z2").toString());
                      String a = params.get("a").toString();
                      announceCuboid(x1, y1, z1, x2, y2, z2, a);
                    }
                    case "announce-sphere" -> {
                      double x = Double.parseDouble(params.get("x").toString());
                      double y = Double.parseDouble(params.get("y").toString());
                      double z = Double.parseDouble(params.get("z").toString());
                      double r = Double.parseDouble(params.get("r").toString());
                      String a = params.get("a").toString();
                      announceSphere(x, y, z, r, a);
                    }
                    case "setvar" -> {
                      String i = params.get("id").toString();
                      String v = params.get("var").toString();
                      setVariable(i, v);
                    }
                    case "setticker" -> {
                      String i = params.get("id").toString();
                      String tm = params.get("tick-mode").toString();
                      long interval = Long.parseLong(params.get("interval").toString());
                      setTicker(i, tm, interval);
                    }
                    case "setblock" -> {
                      int x = Integer.parseInt(params.get("x").toString());
                      int y = Integer.parseInt(params.get("y").toString());
                      int z = Integer.parseInt(params.get("z").toString());
                      String type = params.get("type").toString();
                      Location location = new Location(group.getWorld(), x, y, z);
                      location.getBlock().setType(Material.valueOf(type));
                    }
                    case "playsound" -> {
                      String sound = params.get("sound").toString();
                      String source = params.get("source").toString();
                      float volume = Float.parseFloat(params.get("volume").toString());
                      float pitch = Float.parseFloat(params.get("pitch").toString());
                      playSound(group, sound, source, volume, pitch);
                    }
                    case "playsound-cuboid" -> {
                      double x1 = Double.parseDouble(params.get("x1").toString());
                      double y1 = Double.parseDouble(params.get("y1").toString());
                      double z1 = Double.parseDouble(params.get("z1").toString());
                      double x2 = Double.parseDouble(params.get("x2").toString());
                      double y2 = Double.parseDouble(params.get("y2").toString());
                      double z2 = Double.parseDouble(params.get("z2").toString());
                      String sound = params.get("sound").toString();
                      String source = params.get("source").toString();
                      float volume = Float.parseFloat(params.get("volume").toString());
                      float pitch = Float.parseFloat(params.get("pitch").toString());
                      playSoundCuboid(x1, y1, z1, x2, y2, z2, sound, source, volume, pitch);
                    }
                    case "playsound-sphere" -> {
                      double x = Double.parseDouble(params.get("x").toString());
                      double y = Double.parseDouble(params.get("y").toString());
                      double z = Double.parseDouble(params.get("z").toString());
                      double r = Double.parseDouble(params.get("r").toString());
                      String sound = params.get("sound").toString();
                      String source = params.get("source").toString();
                      float volume = Float.parseFloat(params.get("volume").toString());
                      float pitch = Float.parseFloat(params.get("pitch").toString());
                      playSoundSphere(x, y, z, r, sound, source, volume, pitch);
                    }
                  }
    
                }
  
              }
            }, delay);
  
          } else plugin.getLogger().warning("BarrelStation's barrel book is in the wrong format!");
        }
  
      }
  
      // Do what the station sign does
      super.execute(info);
      
    }
  }
  
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    if (event.getPlayer().hasPermission("mikestcaddons.barrelstation")) {
      return SignBuildOptions.create().setName("barrelstation").setDescription("stop, wait and launch trains, and update blocks").handle(event.getPlayer());
    } else {
      event.setCancelled(true);
      return false;
    }
  }
  
  
  private long parseTicks(String timestring) {
    long rval = 0;
    if (!LogicUtil.nullOrEmpty(timestring)) {
      String[] parts = timestring.split(":");
      if (parts.length == 1) {
        //Seconds display only
        rval = (long) (ParseUtil.parseDouble(parts[0], 0.0) * 20);
      } else if (parts.length == 2) {
        //Min:Sec
        rval = ParseUtil.parseLong(parts[0], 0) * 1200;
        rval += ParseUtil.parseLong(parts[1], 0) * 20;
      } else if (parts.length == 3) {
        //Hour:Min:Sec
        rval = ParseUtil.parseLong(parts[0], 0) * 72000;
        rval += ParseUtil.parseLong(parts[1], 0) * 1200;
        rval += ParseUtil.parseLong(parts[2], 0) * 20;
      }
    }
    return rval;
  }

  
  
  // Announce to all players in a train
  private void announce(MinecartGroup group, String message) {
    for (MinecartMember<?> member : group) {
      announce(member, message);
    }
  }
  
  // Helper method for above method
  private void announce(MinecartMember<?> member, String message) {
    member.getEntity().getPlayerPassengers().forEach(player -> TrainCarts.sendMessage(player, ChatColor.translateAlternateColorCodes('&', message)));
  }
  
  // Sends an announcement in a cuboid area
  private void announceCuboid(double x1, double y1, double z1, double x2, double y2, double z2, String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Location location = player.getLocation();
      if (Math.abs(x1 - location.getX()) < Math.abs(x1 - x2) && Math.abs(y1 - location.getY()) < Math.abs(y1 - y2) && Math.abs(z1 - location.getZ()) < Math.abs(z1 - z2) )
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
  }
  
  // Sends an announcement in a spherical area
  private void announceSphere(double x, double y, double z, double r, String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Location location = player.getLocation();
      if ((location.getX()-x)*(location.getX()-x) + (location.getY()-y)*(location.getY()-y) + (location.getZ()-z)*(location.getZ()-z) <= r*r)
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
  }
  
  // Sets a certain SignLink variable
  private void setVariable(String identifier, String value) {
    Variables.get(identifier).set(ChatColor.translateAlternateColorCodes('&', value));
  }
  
  // Sets the ticker method for a SignLink variable
  private void setTicker(String identifier, String tickMode, long interval) {
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
  private void playSound(MinecartGroup group, String sound, String source, float volume, float pitch) {
    group.forEach(member -> playSound(member, sound, source, volume, pitch));
  }
  
  // Helper method for above method
  private void playSound(MinecartMember<?> member, @Subst("minecraft") String sound, String source, float volume, float pitch) {
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
  private void _playSound(Player player, @Subst("minecraft") String sound, String source, float volume, float pitch) {
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
  
  private void playSoundCuboid(double x1, double y1, double z1, double x2, double y2, double z2, String sound, String source, float volume, float pitch) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Location location = player.getLocation();
      if (Math.abs(x1-location.getX()) < Math.abs(x1-x2) && Math.abs(y1-location.getY()) < Math.abs(y1-y2) && Math.abs(z1-location.getZ()) < Math.abs(z1-z2))
        _playSound(player, sound, source, volume, pitch);
    }
  }
  
  private void playSoundSphere(double x, double y, double z, double r, String sound, String source, float volume, float pitch) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Location location = player.getLocation();
      if ((location.getX()-x)*(location.getX()-x) + (location.getY()-y)*(location.getY()-y) + (location.getZ()-z)*(location.getZ()-z) <= r*r)
        _playSound(player, sound, source, volume, pitch);
    }
  }
  
}