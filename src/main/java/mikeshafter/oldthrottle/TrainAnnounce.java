package mikeshafter.oldthrottle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.signactions.SignActionAnnounce;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;


public class TrainAnnounce implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (command.getName().equalsIgnoreCase("ta")){
      if (sender instanceof Player && sender.hasPermission("OldThrottle.ta")){
        Player player = (Player) sender;
        player.sendMessage("it works! (debug code)");
        String message = String.join(" ", args);
        try {
          SignActionAnnounce.sendMessage(null, MinecartGroupStore.get(player), message);
        } catch (Exception e) {
          player.sendMessage("This command can only be used in a train!");
          return false;
        }
        return true;
      }
    }
    
    else if (command.getName().equalsIgnoreCase("setunloadedblock")){
      if (args.length == 5) {
        Location blockLoc = new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        if (!blockLoc.getChunk().isLoaded())
          blockLoc.getChunk().load();
        blockLoc.getBlock().setType(Objects.requireNonNull(Material.getMaterial(args[4].toUpperCase())));
        if (blockLoc.getBlock().getType() == Material.getMaterial(args[4].toUpperCase())) {
          sender.sendMessage(String.format("Placed %s at world %s, x=%s y=%s z=%s]", args[4], args[0], args[1], args[2], args[3]));
          blockLoc.getChunk().unload();
          return true;
        }
      }
    }
    
    return false;
  }
}
