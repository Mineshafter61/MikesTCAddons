package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import mikeshafter.mikestcaddons.throttle.ThrottleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class CommandManager implements TabExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
    if (command.getName().equalsIgnoreCase("throttle") && sender instanceof Player player) {
      if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
        ThrottleManager.addThrottle(player);
        return true;
      } else if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
        ThrottleManager.removeThrottle(player);
        return true;
      }
    }
    
    else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player player && args.length > 1) {
      MinecartGroup vehicle = MinecartMemberStore.getFromEntity(player).getGroup();
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
        }
        
        else if (args[0].equalsIgnoreCase("r")) {
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
    } else if (command.getName().equalsIgnoreCase("swap") && sender instanceof Player player) {
      MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
      if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
        for (MinecartMember<?> member : vehicle) swap(member.getAttachments().getRootAttachment());
        
        player.sendMessage("Swapped left and right doors.");
        return true;
      }
    }
    
    return false;
  }
  
  private void swap(Attachment attachment) {
    // Swap for current attachment
    Collection<Animation> animations = attachment.getAnimations();
    for (Animation animation : animations) {
      if (animation.getOptions().getName().equals("door_R")) {
        AnimationOptions options = animation.getOptions();
        options.setName("door_L");
        animation.setOptions(options);
        attachment.clearAnimations();
        attachment.addAnimation(animation);
      } else if (animation.getOptions().getName().equals("door_L")) {
        AnimationOptions options = animation.getOptions();
        options.setName("door_R");
        animation.setOptions(options);
        attachment.clearAnimations();
        attachment.addAnimation(animation);
      }
    }
    // Swap animations for children as well
    if (!attachment.getChildren().isEmpty()) for (Attachment child : attachment.getChildren()) swap(child);
  }
  
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
    if (command.getName().equalsIgnoreCase("throttle") && sender instanceof Player) {
      return Arrays.asList("on", "off");
    } else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player player) {
      if (player.getVehicle() != null && MinecartMemberStore.getFromEntity(player.getVehicle()) != null && MinecartMemberStore.getFromEntity(player).getGroup().getProperties().getOwners().contains(player.getName().toLowerCase())) {
        if (args.length == 1) {
          return Arrays.asList("l", "r");
        } else if (args.length == 2) {
          return Arrays.asList("o", "c");
        }
      }
    } else if (command.getName().equalsIgnoreCase("swap") && sender instanceof Player) {
      return null;
    }
    return null;
  }
}
