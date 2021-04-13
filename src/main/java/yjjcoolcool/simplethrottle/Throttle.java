package yjjcoolcool.simplethrottle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.signactions.SignActionAnnounce;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class Throttle implements Listener, CommandExecutor{
  Float acceleration = 0.001F;    //acceleration per throttle increment in blocks/tick^2
  //stores acceleration only for players that have executed the command
  HashMap<Player, Integer> accelerationHashMap = new HashMap<>();
  //stores speed calculated every tick from acceleration and previous speed
  HashMap<Player, Float> speedHashMap = new HashMap<>();
  //stores current acceleration mode
  HashMap<Player, Byte> modeHashMap = new HashMap<>();
  
  private final Plugin plugin = SimpleThrottle.getPlugin(SimpleThrottle.class);
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
    if (command.getName().equalsIgnoreCase("simplethrottle")){
      if (sender instanceof Player && sender.hasPermission("SimpleThrottle.throttle")){
        FileConfiguration config = plugin.getConfig();
        
        // Switch on Throttle
        if (args.length == 1 && (args[0].equalsIgnoreCase("on")||args[0].equalsIgnoreCase("enable"))){
          Player player = (Player) sender;
          // If it is already on do nothing
          if (accelerationHashMap.get(player) != null){
            sender.sendMessage(ChatColor.AQUA+"Simple Throttle has already been turned on.");
          }
          // Else do something; include player in playerSpeed and playerAcceleration hashmaps
          else {
            speedHashMap.put(player, 0F);
            accelerationHashMap.put(player, 0);
            modeHashMap.put(player, (byte) 0);
            sender.sendMessage(ChatColor.AQUA+"Simple Throttle has been enabled.");
            // Store inventory
            for (int i = 0; i < 41; i++){
              config.set("inv."+player.getName()+"."+i, player.getInventory().getItem(i));
            }
            plugin.saveConfig();
          }
      
          // Make items into SimpleThrottle controls
          invItemsPg1(player);
          return true;
        }
    
        // Switch off throttle
        else if (args.length == 1 && (args[0].equalsIgnoreCase("off")||args[0].equalsIgnoreCase("disable"))){
          Player player = (Player) sender;
          //but do nothing if throttle was already off
          if (accelerationHashMap.get(player) == null){
            sender.sendMessage(ChatColor.AQUA+"Simple Throttle has already been turned off.");
          } else {
            //remove speed and acceleration hashmap entry
            speedHashMap.remove(player);
            accelerationHashMap.remove(player);
            modeHashMap.remove(player);
            //restore inventory
            for (int i = 0; i < 41; i++){
              player.getInventory().setItem(i, (ItemStack) config.get("inv."+player.getName()+"."+i));
            }
            //send message to player
            sender.sendMessage(ChatColor.AQUA+"Simple Throttle turned off");
          }
          return true;
        }
      }
    }
    
    return false;
  }
  
  public void invItemsPg1(Player player) {
    ItemStack item = new ItemStack(Material.BARRIER, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.setDisplayName("Emergency Brake");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(0, item);
    item.setType(Material.RED_CONCRETE);
    itemMeta.setDisplayName("Brake - Power 3");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(1, item);
    item.setType(Material.ORANGE_TERRACOTTA);
    itemMeta.setDisplayName("Brake - Power 2");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(2, item);
    item.setType(Material.ORANGE_CONCRETE);
    itemMeta.setDisplayName("Brake - Power 1");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(3, item);
    item.setType(Material.YELLOW_CONCRETE);
    itemMeta.setDisplayName("Idle");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(4, item);
    item.setType(Material.GREEN_CONCRETE);
    itemMeta.setDisplayName("Accelerate - Power 1");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(5, item);
    item.setType(Material.LIME_TERRACOTTA);
    itemMeta.setDisplayName("Accelerate - Power 2");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(6, item);
    item.setType(Material.LIME_TERRACOTTA);
    itemMeta.setDisplayName("Accelerate - Power 3");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(7, item);
    item.setType(Material.COMMAND_BLOCK_MINECART);
    itemMeta.setDisplayName("Auto Zone");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(8, item);
  }
  
  @EventHandler
  public void cancelDrop(PlayerDropItemEvent event){
    if (speedHashMap.containsKey(event.getPlayer())) event.setCancelled(true);
  }
  
  @EventHandler
  public void noDie(PlayerDeathEvent event){
    if (speedHashMap.containsKey(event.getEntity())) {
      event.setKeepInventory(true);
    }
  }
  
  @EventHandler
  public void quit(PlayerQuitEvent event){
    Player player = event.getPlayer();
    if (speedHashMap.containsKey(player)) {
      speedHashMap.remove(player);
      accelerationHashMap.remove(player);
      modeHashMap.remove(player);
      //restore inventory
      for (int i = 0; i < 41; i++){
        player.getInventory().setItem(i, (ItemStack) plugin.getConfig().get("inv."+player.getName()+"."+i));
      }
    }
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event){
    Action action = event.getAction(); // Instance of action
    Player player = event.getPlayer(); // Instance of player.
    if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && speedHashMap.containsKey(player)){
      if (player.getInventory().getItemInMainHand().getItemMeta() != null){
        String mode = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        switch (mode){
          case "Brake - Power 1":
            modeHashMap.put(player, (byte) 0);
            accelerationHashMap.put(player, -1);
            break;
          case "Brake - Power 2":
              modeHashMap.put(player, (byte) 0);
              accelerationHashMap.put(player, -2);
              break;
          case "Brake - Power 3":
              modeHashMap.put(player, (byte) 0);
              accelerationHashMap.put(player, -3);
              break;
          case "Emergency Brake":
            modeHashMap.put(player, (byte) 0);
            speedHashMap.put(player, 0F);
            accelerationHashMap.put(player, 0);
            break;
          case "Off and Release":
            if (speedHashMap.get(player) < 0.1f) player.performCommand("train launch 15");
            modeHashMap.put(player, (byte) 0);
            accelerationHashMap.put(player, 0);
            break;
          case "Accelerate - Power 1":
            modeHashMap.put(player, (byte) 1);
            break;
          case "Accelerate - Power 2":
            modeHashMap.put(player, (byte) 2);
            break;
          case "Accelerate - Power 3":
            modeHashMap.put(player, (byte) 3);
            break;
          case "Auto Zone":
            modeHashMap.put(player, (byte) 0);
            speedHashMap.put(player, Float.valueOf(String.format("%.1f", speedHashMap.get(player))));
            accelerationHashMap.put(player, 0);
            break; 
          case "More":
            //TODO: Inventory Page 2
            break;
        }
      }
      event.setCancelled(true);
    }
    if (action == Action.RIGHT_CLICK_BLOCK && speedHashMap.containsKey(player)) // Make items into SimpleThrottle controls
      invItemsPg1(player);
  }
  
  public void repeatThrottle() {
    for (Player player : accelerationHashMap.keySet()){
      //get properties of the cart the player is currently editing
      CartProperties cartProperties = CartProperties.getEditing(player);
      //check if we got cart properties
      if (cartProperties != null){
        //if we can, get properties from the train the cart is part of
        TrainProperties properties = cartProperties.getTrainProperties();
        //set acceleration
        if (modeHashMap.get(player) == (byte) 1){
        	if (speedHashMap.get(player) < 2.0) accelerationHashMap.put(player, 1);
            else accelerationHashMap.put(player, 0);
        }
        else if (modeHashMap.get(player) == (byte) 2){
        	if (speedHashMap.get(player) < 2.0) accelerationHashMap.put(player, 2);
            else accelerationHashMap.put(player, 0);
          /*if (speedHashMap.get(player) < 0.3) accelerationHashMap.put(player, 0);
          else if (speedHashMap.get(player) < 0.6) accelerationHashMap.put(player, 3);
          else if (speedHashMap.get(player) < 0.9) accelerationHashMap.put(player, 2);
          else accelerationHashMap.put(player, 1);*/
        }
        else if (modeHashMap.get(player) == (byte) 3){
        	if (speedHashMap.get(player) < 2.0) accelerationHashMap.put(player, 3);
            else accelerationHashMap.put(player, 0);
          /*if (speedHashMap.get(player) < 0.5) accelerationHashMap.put(player, 0);
          else if (speedHashMap.get(player) < 0.7) accelerationHashMap.put(player, 5);
          else if (speedHashMap.get(player) < 1.0) accelerationHashMap.put(player, 4);
          else if (speedHashMap.get(player) < 1.2) accelerationHashMap.put(player, 3);
          else accelerationHashMap.put(player, 2);*/
        }
        //save current speed in a variable to make working with it easier, and update it
        float currentSpeed = Math.max(speedHashMap.get(player)+(accelerationHashMap.get(player)*acceleration), 0.0F);
        if (currentSpeed>2.0) currentSpeed = 2.0f; //Limit the speed to 2.0
        //update speed limit of the train
        properties.setSpeedLimit(currentSpeed);
        //update new current speed
        speedHashMap.put(player, currentSpeed);
        //send speed to player
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN+"Current speed: "+ChatColor.YELLOW+String.format("%.3f", currentSpeed)+ChatColor.GREEN+"m/t"));
      }
    }
  }
}
