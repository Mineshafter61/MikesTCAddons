package mikeshafter.oldthrottle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.components.SpeedAheadWaiter;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Objects;

import static java.lang.Math.round;

public final class OldThrottle extends JavaPlugin implements Listener{
  
  Float acceleration = 0.001F;    //acceleration per throttle increment in blocks/tick^2
  //stores acceleration only for players that have executed the command
  HashMap<Player, Integer> accelerationHashMap = new HashMap<>();
  //stores speed calculated every tick from acceleration and previous speed
  HashMap<Player, Float> speedHashMap = new HashMap<>();
  
  Plugin plugin = this;
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"OldThrottle has been invoked!");
    getConfig().options().copyDefaults(true);
    saveConfig();
  
    getServer().getPluginManager().registerEvents(this, this);
  
    BukkitScheduler scheduler = getServer().getScheduler();
  
    scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
      @Override
      public void run(){
        for (Player player : accelerationHashMap.keySet()){
          //get properties of the cart the player is currently editing
          CartProperties cartProperties = CartProperties.getEditing(player);
          //check if we got cart properties
          if (cartProperties != null) {
            //if we can, get properties from the train the cart is part of
            TrainProperties properties = cartProperties.getTrainProperties();
            //save current speed in a variable to make working with it easier, and update it
            float currentSpeed = Math.max(speedHashMap.get(player)+(accelerationHashMap.get(player)*acceleration), 0.0F);
            //update speed limit of the train
            properties.setSpeedLimit(currentSpeed);
            //update new current speed
            speedHashMap.put(player, currentSpeed);
            //send speed to player
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN+"Your current speed: "+ChatColor.YELLOW+String.format("%.3f",currentSpeed)+ChatColor.GREEN+"m/t.st"));
          }
        }
      }
    }, 0, 1);
  }
  
  @Override
  public void onDisable(){
    // Plugin shutdown logic
    saveConfig();
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"OldThrottle has been disabled.");
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(command.getName().equalsIgnoreCase("throttle")){
      FileConfiguration config = this.getConfig();
      //if argument is "off" remove all player information to turn throttle off
      if(args.length == 1 && args[0].equalsIgnoreCase("off")) {
        Player player = (Player) sender;
        //but do nothing if throttle was already off
        if (accelerationHashMap.remove(player) == null) {
          sender.sendMessage(ChatColor.AQUA + "Throttle has already been turned off.");
        }else{
          //remove speed and acceleration hashmap entry
          speedHashMap.remove(player);
          accelerationHashMap.remove(player);
          //restore inventory
          player.getInventory().setContents( (ItemStack[]) Objects.requireNonNull(config.get(player.getName())));
          config.set(player.getName(), null);
          //send message to player
          sender.sendMessage(ChatColor.AQUA + "Throttle turned off");
        }
        return true; //command executed successfully
      }
      //if argument is an integer between 2 and 8
      else if(args.length == 1 && args[0].equalsIgnoreCase("on")){
        Player player = (Player) sender;
        // include player in playerSpeed and playerAcceleration hashmaps
        speedHashMap.put(player, 0F);
        accelerationHashMap.put(player, 0);
        sender.sendMessage(ChatColor.AQUA + "Throttle has been enabled.");
        // Store inventory
        config.set(player.getName(), player.getInventory().getContents());
        // Put items in inventory
        ItemStack item = new ItemStack(Material.BLUE_CONCRETE, 1);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("Add Brake");
        item.setItemMeta(itemMeta);
        player.getInventory().setItem(0, item);
        
        item = new ItemStack(Material.RED_CONCRETE, 1);
        itemMeta.setDisplayName("Emergency Brake"); item.setItemMeta(itemMeta);
        player.getInventory().setItem(1, item);
  
        item = new ItemStack(Material.LIME_CONCRETE, 1);
        itemMeta.setDisplayName("Off and Release"); item.setItemMeta(itemMeta);
        player.getInventory().setItem(2, item);
  
        item = new ItemStack(Material.PURPLE_TERRACOTTA, 1);
        itemMeta.setDisplayName("Shunt"); item.setItemMeta(itemMeta);
        player.getInventory().setItem(3, item);
  
        item = new ItemStack(Material.PURPLE_CONCRETE, 1);
        itemMeta.setDisplayName("Series"); item.setItemMeta(itemMeta);
        player.getInventory().setItem(4, item);
  
        item = new ItemStack(Material.MAGENTA_TERRACOTTA, 1);
        itemMeta.setDisplayName("Parallel"); item.setItemMeta(itemMeta);
        player.getInventory().setItem(5, item);
        
        item = new ItemStack(Material.ORANGE_CONCRETE, 1);
        itemMeta.setDisplayName("Auto Zone"); item.setItemMeta(itemMeta);
        player.getInventory().setItem(6, item);
        
        player.getInventory().setItem(7, new ItemStack(Material.AIR, 0));
  
        player.getInventory().setItem(8, new ItemStack(Material.AIR, 0));
        
        return true; //command executed successfully
      } else {
        sender.sendMessage(ChatColor.AQUA + "Incorrect argument provided. Provide an integer between 2 and 8. 5 is neutral.");
        return false;  //command failed to execute
      }
      
    }
    return false;
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event){
    Action action = event.getAction(); // Instance of action
    Player player = event.getPlayer(); // Instance of player.
    if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
      if (speedHashMap.containsKey(player) && player.getInventory().getItemInMainHand().getItemMeta() != null){
        String mode = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        switch (mode){
          case "Add Brake":
            accelerationHashMap.put(player, accelerationHashMap.get(player)-1);
            break;
          case "Emergency Brake":
            speedHashMap.put(player, 0F);
            accelerationHashMap.put(player, 0);
            break;
          case "Off and Release":
            accelerationHashMap.put(player, 0);
            break;
          case "Shunt":
            accelerationHashMap.put(player, 1);
            break;
          case "Series":
            accelerationHashMap.put(player, 3);
            break;
          case "Parallel":
            accelerationHashMap.put(player, 4);
            break;
          case "Auto Zone":
            speedHashMap.put(player, Float.valueOf(String.format("%.1f",speedHashMap.get(player))));
            accelerationHashMap.put(player, 0);
        }
      }
    }
  }
}
