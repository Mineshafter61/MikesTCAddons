package mikeshafter.oldthrottle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TrainAnnounce implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
    if (command.getName().equalsIgnoreCase("ta")){

      // If sent from player
      if (sender instanceof Player && sender.hasPermission("OldThrottle.ta")){
        Player player = (Player) sender;
        String message = String.join(" ", args);
        player.sendMessage("DEBUG: "+message); // TODO: DEBUG

        // Get the train the player is in
        MinecartGroupStore store = new MinecartGroupStore();
        for (MinecartMember<?> member : store.get(player) {

          // Get every player passenger
          for (Player passenger : member.getEntity().getPlayerPassengers()) {
           passenger.sendMessage(message);
          }
        }
        return true;

        // If sent from command block
      } else if (sender instanceof BlockCommandSender) {
        BlockCommandSender commandSender = (BlockCommandSender) sender;
        Block commandBlock = commandSender.getBlock();
        String message = String.join(" ", args);

        // Get every player within 3 blocks of the cmd block
        // TODO: change this to nearest player within 3 blocks
        for (Player player : Bukkit.getOnlinePlayers())
          if (commandBlock.getLocation().distance(player.getLocation()) <= 3) {
            player.sendMessage("DEBUG: "+message); // TODO: DEBUG

            // Get the train the player is in
            for (MinecartMember<?> member : MinecartGroupStore.get(player)) {

              // Get every player passenger
              for (Player passenger : member.getEntity().getPlayerPassengers()) {
                passenger.sendMessage(message);
              }
            }
            return true;
          }
      }
    }

    /*
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
    */

    return false;
  }
}
