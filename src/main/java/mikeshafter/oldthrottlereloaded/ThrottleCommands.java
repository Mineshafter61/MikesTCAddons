package mikeshafter.oldthrottlereloaded;

import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class ThrottleCommands implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
    
    if (command.getName().equalsIgnoreCase("throttle") && sender instanceof Player player) {
      if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
        Throttle.addPlayer(player);
        return true;
      }
      else if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
        Throttle.removePlayer(player);
        return true;
      }
    }
    
    else if (command.getName().equalsIgnoreCase("door") && sender instanceof Player player && args.length > 1) {
      MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
      if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
        if (args[0].equalsIgnoreCase("l")) {
          AnimationOptions options = new AnimationOptions();
          
          if (args[1].equalsIgnoreCase("o")) {
            options.setSpeed(0.5);
          } else {
            options.setSpeed(-0.5);
          }
          
          options.setName("door_L");
          vehicle.playNamedAnimation(options);
          return true;
        }
        
        else if (args[0].equalsIgnoreCase("r")) {
          AnimationOptions options = new AnimationOptions();
  
          if (args[1].equalsIgnoreCase("o")) {
            options.setSpeed(0.5);
          } else {
            options.setSpeed(-0.5);
          }
  
          options.setName("door_L");
          vehicle.playNamedAnimation(options);
          return true;
        }
      }
    }
    
    else if (command.getName().equalsIgnoreCase("swap") && sender instanceof Player player ) {
      MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
      if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase())) {
        for (MinecartMember<?> member : vehicle) {
          Collection<Animation> animations = member.getAttachments().getRootAttachment().getAnimations();
          for (Animation animation : animations) {
            if (animation.getOptions().getName().equals("door_L")) {
              AnimationOptions options = animation.getOptions();
              options.setName("door_R");
              animation.setOptions(options);
            } else if (animation.getOptions().getName().equals("door_R")) {
              AnimationOptions options = animation.getOptions();
              options.setName("door_L");
              animation.setOptions(options);
            }
          }
        }
        return true;
      }
    }
  
    return false;
  }
}
