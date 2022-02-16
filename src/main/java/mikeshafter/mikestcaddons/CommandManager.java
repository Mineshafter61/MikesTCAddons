package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.CartPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CommandManager implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
  
    // throttle
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
    }
  
    // door
    else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player player && args.length == 2 && sender.hasPermission("mikestcaddons.door")) {
    
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
            } else {
              options.setSpeed(1);
            }
          
            options.setName("door_L");
            vehicle.playNamedAnimation(options);
            return true;
          
            // right side
          } else if (args[0].contains("r")) {
            AnimationOptions options = new AnimationOptions();
          
            // c for close, o for open
            if (args[1].contains("c")) {
              options.setSpeed(-1);
            } else {
              options.setSpeed(1);
            }
          
            options.setName("door_R");
            vehicle.playNamedAnimation(options);
            return true;
          }
        }
      }
    }
  
    // swap
    else if (command.getName().equalsIgnoreCase("swap") && sender instanceof Player player && args.length == 0 && sender.hasPermission("mikestcaddons.swap")) {
    
      // Get the train the player is editing
      if (CartPropertiesStore.getEditing(player).getHolder().getGroup() != null) {
        MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
      
        // Check if the player is an owner
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
        
          // Swap every door animation name
          for (MinecartMember<?> member : vehicle) SignActionSwap.swap(member.getProperties().getModel().getConfig());
          player.sendMessage("Swapped left and right doors.");
        }
      } else player.sendMessage("You need to own a train first!");
      return true;
    
    }
  
    // decouple
    else if (command.getName().equalsIgnoreCase("decouple") && sender instanceof Player player && args.length == 1 && sender.hasPermission("mikestcaddons.decouple")) {
    
      // Get the train the player is editing
      if (CartPropertiesStore.getEditing(player).getHolder().getGroup() != null) {
        MinecartGroup vehicle = CartPropertiesStore.getEditing(player).getHolder().getGroup();
      
        // Check if the player is an owner
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
        
          int toDecouple = Integer.parseInt(args[0]);  // Get the number of carts to decouple
          List<MinecartMember<?>> members = vehicle.stream().toList();  // Make the train into a list for easier editing
          TrainProperties properties = vehicle.getProperties();  // Get properties
          int size = members.size();  // Get size
          MinecartMember<?>[] newGroup = new MinecartMember<?>[Math.abs(toDecouple)];  // New train from existing
        
        
          // if the number to decouple is negative, decouple from the rear:
          if (toDecouple < 0) {
            toDecouple = -toDecouple;
          
            // Remove carts sequentially
            int j = 0;
            for (int i = size-1; i > size-toDecouple; i--) {
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
    
    }
  
    return false;
  }
  
}
