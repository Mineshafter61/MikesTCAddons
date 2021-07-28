package mikeshafter.oldthrottle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
  
        // Check for ownership
        MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
        if (vehicle.getProperties().getOwners().contains(player.getName().toLowerCase()))
    
          // Get the train the player is in
          for (MinecartMember<?> member : vehicle)
      
            // Get every player passenger
            for (Player passenger : member.getEntity().getPlayerPassengers())
              passenger.sendMessage(message);
  
        return true;
  
  
        // If sent from command block
      } else if (sender instanceof BlockCommandSender) {
        BlockCommandSender commandSender = (BlockCommandSender) sender;
        Block commandBlock = commandSender.getBlock();
        AdvancedMessage message = advancedMessage(args);
  
        // Check if message has flags
        if (message instanceof RadiusMessage) {
          RadiusMessage radiusMessage = (RadiusMessage) message;
          Location location = radiusMessage instanceof RadiusMessageCoordinates ?
                                  new Location(commandBlock.getWorld(), ((RadiusMessageCoordinates) radiusMessage).getX(), ((RadiusMessageCoordinates) radiusMessage).getY(), ((RadiusMessageCoordinates) radiusMessage).getZ())
                                  : commandBlock.getLocation();
    
          for (Player player : Bukkit.getOnlinePlayers()) {
            if (location.distanceSquared(player.getLocation()) <= radiusMessage.getR()*radiusMessage.getR()) {
        
              // Get the train the player is in
              for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
          
                // Get every player passenger
                for (Player passenger : member.getEntity().getPlayerPassengers()) {
                  passenger.sendMessage(message.toString());
                }
              }
              return true;
            }
          }
        } else if (message instanceof AdvancedMessageBox) {
          AdvancedMessageBox messageBox = (AdvancedMessageBox) message;
          int x = messageBox.getX();
          int y = messageBox.getY();
          int z = messageBox.getZ();
          int dx = messageBox.getDx();
          int dy = messageBox.getDy();
          int dz = messageBox.getDz();
    
          for (Player player : Bukkit.getOnlinePlayers()) {
            if (Math.abs(x-player.getLocation().getX()) <= dx && Math.abs(y-player.getLocation().getY()) <= dy && Math.abs(z-player.getLocation().getZ()) <= dz) {
        
              // Get the train the player is in
              for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
          
                // Get every player passenger
                for (Player passenger : member.getEntity().getPlayerPassengers()) {
                  sendJsonMessage(passenger, message.toString());
                }
              }
              return true;
            }
          }
        }
  
        // Get every player within 3 blocks of the cmd block
        // .distanceSquared() takes up lesser ram than .distance()
  
      }
    }


    // Command format: /tj <message>
    // Same as above, but this sends raw json
    else if (command.getName().equalsIgnoreCase("tj")) {
  
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
        AdvancedMessage message = advancedMessage(args);
    
        // Check if message has flags
        if (message instanceof RadiusMessage) {
          RadiusMessage radiusMessage = (RadiusMessage) message;
          Location location = radiusMessage instanceof RadiusMessageCoordinates ?
                                  new Location(commandBlock.getWorld(), ((RadiusMessageCoordinates) radiusMessage).getX(), ((RadiusMessageCoordinates) radiusMessage).getY(), ((RadiusMessageCoordinates) radiusMessage).getZ())
                                  : commandBlock.getLocation();
      
          for (Player player : Bukkit.getOnlinePlayers()) {
            if (location.distanceSquared(player.getLocation()) <= radiusMessage.getR()*radiusMessage.getR()) {
          
              // Get the train the player is in
              for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
            
                // Get every player passenger
                for (Player passenger : member.getEntity().getPlayerPassengers()) {
                  sendJsonMessage(passenger, message.toString());
                }
              }
              return true;
            }
          }
        } else if (message instanceof AdvancedMessageBox) {
          AdvancedMessageBox messageBox = (AdvancedMessageBox) message;
          int x = messageBox.getX();
          int y = messageBox.getY();
          int z = messageBox.getZ();
          int dx = messageBox.getDx();
          int dy = messageBox.getDy();
          int dz = messageBox.getDz();
      
          for (Player player : Bukkit.getOnlinePlayers()) {
            if (Math.abs(x-player.getLocation().getX()) <= dx && Math.abs(y-player.getLocation().getY()) <= dy && Math.abs(z-player.getLocation().getZ()) <= dz) {
          
              // Get the train the player is in
              for (MinecartMember<?> member : MinecartGroupStore.get(player.getVehicle())) {
            
                // Get every player passenger
                for (Player passenger : member.getEntity().getPlayerPassengers()) {
                  sendJsonMessage(passenger, message.toString());
                }
              }
              return true;
            }
          }
        }
    
        // Get every player within 3 blocks of the cmd block
        // .distanceSquared() takes up lesser ram than .distance()
    
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
  
  private AdvancedMessage advancedMessage(String[] args) {
    switch (args[0]) {
      case "-r":
        return new RadiusMessage(String.join(" ", Arrays.copyOfRange(args, 2, args.length)), Integer.parseUnsignedInt(args[1]));
      case "-rxyz":
        return new RadiusMessageCoordinates(
            String.join(" ", Arrays.copyOfRange(args, 2, args.length)),
            Integer.parseUnsignedInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])
        );
      case "-d":
        return new AdvancedMessageBox(
            String.join(" ", Arrays.copyOfRange(args, 2, args.length)),
            Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6])
        );
      default:
        return new AdvancedMessage(String.join(" ", args));
    }
  }
  
  private void sendJsonMessage(Player player, String message) {
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+player.getName()+" "+message);
  }
}


class AdvancedMessage {
  private final String message;
  
  AdvancedMessage(String message) {
    this.message = message;
  }
  
  public String toString() {
    return message;
  }
}


class RadiusMessage extends AdvancedMessage {
  private final int r;
  
  public RadiusMessage(String message, int r) {
    super(message);
    this.r = r;
  }
  
  public int getR() {
    return r;
  }
}


class RadiusMessageCoordinates extends RadiusMessage {
  private final int x, y, z;
  
  public RadiusMessageCoordinates(String message, int r, int x, int y, int z) {
    super(message, r);
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public int getZ() {
    return z;
  }
}


class AdvancedMessageBox extends AdvancedMessage {
  private final int x, y, z, dx, dy, dz;
  
  public AdvancedMessageBox(String message, int x, int y, int z, int dx, int dy, int dz) {
    super(message);
    this.x = x;
    this.y = y;
    this.z = z;
    this.dx = dx;
    this.dy = dy;
    this.dz = dz;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public int getZ() {
    return z;
  }
  
  public int getDx() {
    return dx;
  }
  
  public int getDy() {
    return dy;
  }
  
  public int getDz() {
    return dz;
  }
}