package mikeshafter.mikestcaddons.signactions;

import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import mikeshafter.mikestcaddons.MikesTCAddons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.util.*;

import static mikeshafter.mikestcaddons.util.BarrelUtil.*;


public class SignActionBarrelRun extends SignAction {
  // Get plugin
  private final Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
  
  @Override
  public boolean match(SignActionEvent info) {
    return info.isType("barrelrun", "specialrun") && info.getMode() != SignActionMode.NONE;
  }
  
  
  @Override
  public void execute(SignActionEvent info) {
    
    if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
      if (!info.hasRailedMember() || !info.isPowered()) return;
      barrel(info, info.getGroup());
    } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
      for (MinecartGroup group : info.getRCTrainGroups()) {
        barrel(info, group);
      }
    }
    
  }
  
  
  public void barrel(SignActionEvent info, MinecartGroup group) {
    
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
            builder.append(pageText.content()).append("\n");
          }
        }
      }
      // Code to be parsed
      String[] content = builder.toString().split("\n");
  
      // All code must now be done in macros written in config.yml.
      // Each line in the book corresponds to a macro.
      boolean inMacro;
      String macroText = "";
      ArrayList<String> allMacros = new ArrayList<>();
      
      for (String line : content) {
        // string without colons are macros
        // string with colons are arguments in the form <argument>:<value>
        inMacro = line.contains(":");

        if (!inMacro) {
          // add the previous macroText to allMacros before starting a new one (if macroText is not empty)
          if (!Objects.equals(macroText, "")) allMacros.add(macroText);
          // start a new macroText
          // if the macro doesn't exist, quit
          if (plugin.getConfig().get(line) == null) return;
          macroText = Objects.requireNonNull(plugin.getConfig().get(line)).toString();  // get the yaml to replace
        }
        else {  // replace text in the yaml
          macroText = macroText.replaceAll("%"+line.split(":")[0]+"%", line.split(":")[1]);
        }
      }

      // load through every macro using forEach like a gigachad
      allMacros.forEach(macro -> loadMacro(macro, group));
    }
    
  }
  
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    if (event.getPlayer().hasPermission("mikestcaddons.barrelrun")) {
      return SignBuildOptions.create().setName("barrelrun").setDescription("update blocks").handle(event.getPlayer());
    } else {
      event.setCancelled(true);
      return false;
    }
  }

  // put code in a function cuz i hate myself
  private void loadMacro(String macro, MinecartGroup group) {
    try {
      Yaml yaml = new Yaml();
      Map<String, Object> data = yaml.load(macro);
  
      if (data == null) return;
  
      // Top level allowed keys:
      //   A number indicating the time after the train has stopped at the station, and/or a statement.
      // Negative numbers are not allowed.
      // Use parseTicks() to get the delay in a long.
      for (String key : data.keySet()) {
        // Delay and statements are separated by a " ". The delay is compulsory.
        String[] parts = key.split(" ");
        long delay = parseTicks(parts[0]);
        // Parse tags
        List<String> tags = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
        
        // Check if group has the statements and syntax is correct
        if (group.getProperties().getTags().containsAll(tags) && data.get(key) instanceof Map dataMap) {
          
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
                  case "animate" -> {
                    AnimationOptions animationOptions = new AnimationOptions();
                    animationOptions.setName(params.get("name").toString());
                    if (params.get("speed") != null)
                      animationOptions.setSpeed(Double.parseDouble(params.get("speed").toString()));
                    if (params.get("delay") != null)
                      animationOptions.setDelay(Double.parseDouble(params.get("delay").toString()));
                    if (params.get("looped") != null)
                      animationOptions.setLooped(Boolean.parseBoolean(params.get("looped").toString()));
                    if (params.get("queue") != null)
                      animationOptions.setQueue(Boolean.parseBoolean(params.get("queue").toString()));
                    if (params.get("reset") != null)
                      animationOptions.setReset(Boolean.parseBoolean(params.get("reset").toString()));
                    if (params.get("scene") != null && !(params.get("scene") instanceof List))
                      animationOptions.setScene(params.get("scene").toString());
                    else if (params.get("scene") != null && params.get("scene") instanceof List l)
                      animationOptions.setScene(l.get(0).toString(), l.get(1).toString());
                    group.playNamedAnimation(animationOptions);
                  }
                  case "open" -> {
                    World world = group.getWorld();
                    int x = Integer.parseInt(params.get("x").toString());
                    int y = Integer.parseInt(params.get("y").toString());
                    int z = Integer.parseInt(params.get("z").toString());
                    BlockFace direction = BlockFace.valueOf(params.get("direction").toString());
                    long time = Long.parseLong(params.get("time").toString());
                    openDoor(world, x, y, z, direction, time);
                  }
                }
                
              }
              
            }
          }, delay);
        } 
          
        else if (!(data.get(key) instanceof Map))
          plugin.getLogger().warning("BarrelRun's barrel book is in the wrong format!");
      }
      
    } catch (ScannerException e) {
      // send error messages to passengers
      group.forEach(m -> m.getEntity().getPlayerPassengers().forEach(p -> p.sendMessage(e.getMessage())));
    }
  }
  
}