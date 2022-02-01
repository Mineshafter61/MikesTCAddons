package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
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
import java.util.Map;


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
          for (MinecartMember<?> member : vehicle) swap(member.getAttachments().getRootAttachment());
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
  
  private void swap(Attachment attachment) {
    // Swap animations for children
    if (!attachment.getChildren().isEmpty()) for (Attachment child : attachment.getChildren()) swap(child);
  
    // Swap for current attachment
    ConfigurationNode node = attachment.getConfig().getNode("animations");
    List<String> animationNames = attachment.getAnimationNamesRecursive();
    Map<String, Animation> animationMap = attachment.getInternalState().animations;
  
    // Swap right doors for left doors
    if (animationNames.contains("door_R")) {
      for (String name : animationNames) {
        if (name.contains("door_R")) {
          // get full name
          Animation animation = animationMap.get(name);
          // change "door_R" to "door_L"
          animationMap.put("door_L"+name.substring(6), animation);
          // remove original name
          animationMap.remove(name);
        }
      }
    }

    // Swap left doors for right doors
    else if (animationNames.contains("door_L")) {
      for (String name : animationNames) {
        if (name.contains("door_L")) {
          // get full name
          Animation animation = animationMap.get(name);
          // change "door_L" to "door_R"
          animationMap.put("door_R"+name.substring(6), animation);
          // remove original name
          animationMap.remove(name);
        }
      }
    }
  
    attachment.onDetached();
    attachment.onLoad(node);
    attachment.onAttached();
    attachment.setFocused(true);
//    for (Iterator<Animation> iterator = animations.iterator(); iterator.hasNext(); ) {
//      Animation animation = iterator.next();
//
//      if (animation.getOptions().getName().equals("door_R")) {
//        AnimationOptions options = animation.getOptions();
//        options.setName("door_L");
//        animation.setOptions(options);
//        attachment.clearAnimations();
//        attachment.addAnimation(animation);
//      } else if (animation.getOptions().getName().equals("door_L")) {
//        AnimationOptions options = animation.getOptions();
//        options.setName("door_R");
//        animation.setOptions(options);
//        attachment.clearAnimations();
//        attachment.addAnimation(animation);
//      }
//    }
  
  
  }
  
}
