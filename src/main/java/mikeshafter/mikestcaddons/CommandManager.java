package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public class CommandManager implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
    if (command.getName().equalsIgnoreCase("throttle") && sender instanceof Player player && args.length == 1 && sender.hasPermission("mikestcaddons.throttle")) {
      if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null) {
        MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
          if (args[0].equalsIgnoreCase("on")) {
            ThrottleManager.addThrottle(player);
            return true;
          } else if (args[0].equalsIgnoreCase("off")) {
            ThrottleManager.removeThrottle(player);
            return true;
          }
        } else {
          player.sendMessage(ChatColor.RED+"Please claim the train first!");
          return true;
        }
      }
    } else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player player && args.length == 2 && sender.hasPermission("mikestcaddons.door")) {
      if (MinecartGroupStore.get(player.getVehicle()) != null) {
        MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
          if (args[0].equalsIgnoreCase("l")) {
            AnimationOptions options = new AnimationOptions();
        
            if (args[1].equalsIgnoreCase("o")) {
              options.setSpeed(1);
            } else {
              options.setSpeed(-1);
            }
          
            options.setName("door_L");
            vehicle.playNamedAnimation(options);
            return true;
          } else if (args[0].equalsIgnoreCase("r")) {
            AnimationOptions options = new AnimationOptions();
          
            if (args[1].equalsIgnoreCase("o")) {
              options.setSpeed(1);
            } else {
              options.setSpeed(-1);
            }
        
            options.setName("door_R");
            vehicle.playNamedAnimation(options);
            return true;
          }
        }
      }
    } else if (command.getName().equalsIgnoreCase("swap") && sender instanceof Player player && args.length == 0 && sender.hasPermission("mikestcaddons.swap")) {
      if (MinecartGroupStore.get(player.getVehicle()) != null) {
        MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
          for (MinecartMember<?> member : vehicle) swap(member.getProperties().getModel().getConfig());
          player.sendMessage("Swapped left and right doors.");
        }
      } else player.sendMessage("You need to own a train first!");
      return true;
  
    } else if (command.getName().equalsIgnoreCase("decouple") && sender instanceof Player player && args.length == 1 && sender.hasPermission("mikestcaddons.decouple")) {
      if (MinecartGroupStore.get(player.getVehicle()) != null) {
        MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
        int toDecouple = Integer.parseInt(args[0]);
        List<MinecartMember<?>> members = vehicle.stream().toList();
        TrainProperties properties = vehicle.getProperties();
        int size = members.size();
        MinecartMember<?>[] newGroup = new MinecartMember<?>[Math.abs(toDecouple)];
    
    
        // if the number to decouple is negative, decouple from the rear:
        if (toDecouple < 0) {
          toDecouple = -toDecouple;
      
          int j = 0;
          for (int i = size-1; i > size-toDecouple; i--) {
            newGroup[j] = members.get(i);
            j++;
            vehicle.remove(i);
          }
      
          MinecartGroupStore.createSplitFrom(properties, newGroup);
          return true;
        }
    
        // else decouple from the front
        else if (toDecouple > 0) {
      
          for (int i = 0; i < toDecouple; i++) {
            newGroup[i] = members.get(i);
          }
          vehicle.subList(0, toDecouple).clear();
  
          MinecartGroupStore.createSplitFrom(properties, newGroup);
          return true;
        }
      }
  
    }
  
    return false;
  }
  
  private ConfigurationNode swap(ConfigurationNode attachmentNode) {
    Set<ConfigurationNode> attachments = attachmentNode.getNode("attachments").getNodes();
    if (attachments != null) {
      for (ConfigurationNode node : attachments) swap(node);
    }
    
    ConfigurationNode animations = attachmentNode.getNode("animations");
    Set<String> animationNames = animations.getKeys();
    
    if (animationNames.contains("door_R")) {
      for (String name : animationNames) {
        if (name.contains("door_R")) {
//          Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
//          plugin.getLogger().info(name);
//          plugin.getLogger().info("door_L"+name.substring(6));
          // set to new node
          animations.set("door_L"+name.substring(6), animations.getNode(name));
          // remove
          animations.remove(name);
          attachmentNode.set("animations", animations);
        }
      }
    }
    
    // Swap left doors for right doors
    else if (animationNames.contains("door_L")) {
      for (String name : animationNames) {
        if (name.contains("door_L")) {
          // set to new node
          animations.set("door_R"+name.substring(6), animations.getNode(name));
          // remove
          animations.remove(name);
          attachmentNode.set("animations", animations);
        }
      }
    }
    
    return attachmentNode;
  }
  
}
