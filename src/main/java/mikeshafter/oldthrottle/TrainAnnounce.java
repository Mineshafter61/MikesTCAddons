package mikeshafter.oldthrottle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TrainAnnounce implements CommandExecutor {
  // Text colour
  private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String s, String[] args) {
    
    // Command format: /ta <message>
    if (command.getName().equalsIgnoreCase("ta")) {
      
      // If sent from player
      if (sender instanceof Player && sender.hasPermission("OldThrottle.ta")) {
        Player player = (Player) sender;
        String message = colourise(String.join(" ", args));
        player.sendMessage("Sent message: "+message);
        
        // Get the train the player is in
        for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
          
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
        // .distanceSquared() takes up lesser ram than .distance()
        for (Player player : Bukkit.getOnlinePlayers())
          if (commandBlock.getLocation().distanceSquared(player.getLocation()) <= 9) {
            
            // Get the train the player is in
            for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
              
              // Get every player passenger
              for (Player passenger : member.getEntity().getPlayerPassengers()) {
                passenger.sendMessage(message);
              }
            }
            return true;
          }
      }
    }
    
    
    // Command format: /tj <message>
    // Same as above, but this sends raw json
    if (command.getName().equalsIgnoreCase("tj")) {
      
      // If sent from player
      if (sender instanceof Player && sender.hasPermission("OldThrottle.tj")) {
        Player player = (Player) sender;
        String message = String.join(" ", args);
        player.sendMessage("Sent message:");
        sendJsonMessage(player, message);
        
        // Get the train the player is in
        for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
          
          // Get every player passenger
          for (Player passenger : member.getEntity().getPlayerPassengers()) {
            sendJsonMessage(passenger, message);
          }
        }
        return true;
        
        // If sent from command block
      } else if (sender instanceof BlockCommandSender) {
        BlockCommandSender commandSender = (BlockCommandSender) sender;
        Block commandBlock = commandSender.getBlock();
        String message = String.join(" ", args);
        
        // Get every player within 3 blocks of the cmd block
        // .distanceSquared() takes up lesser ram than .distance()
        for (Player player : Bukkit.getOnlinePlayers())
          if (commandBlock.getLocation().distanceSquared(player.getLocation()) <= 9) {
            
            // Get the train the player is in
            for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
              
              // Get every player passenger
              for (Player passenger : member.getEntity().getPlayerPassengers()) {
                sendJsonMessage(passenger, message);
              }
            }
            return true;
          }
      }
    }
    
    return false;
  }
  
  public static String colourise(String message) {
    Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', message));
    StringBuffer buffer = new StringBuffer();
    
    while (matcher.find()) {
      matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
    }
    
    return matcher.appendTail(buffer).toString();
  }
  
  public void sendJsonMessage(Player player, String message) {
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "/tellraw "+player.getName()+message);
  }
}
