package mikeshafter.oldthrottle;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Throttle implements Listener, CommandExecutor {
  
  //stores force only for players that have executed the command
  HashMap<Player, Float> forceHashMap = new HashMap<>();
  //stores speed calculated every tick from force and previous speed
  HashMap<Player, Float> speedHashMap = new HashMap<>();
  //stores current force mode
  HashMap<Player, Byte> modeHashMap = new HashMap<>();
  //boss bar for brake
  HashMap<Player, BossBar> brakeHashMap = new HashMap<>();
  //store player inv in ram
  HashMap<Player, ItemStack[]> invHashMap = new HashMap<>();
  
  private final Plugin plugin = OldThrottle.getPlugin(OldThrottle.class);
  
  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
    if (command.getName().equalsIgnoreCase("throttle")) {
      if (sender instanceof Player && sender.hasPermission("OldThrottle.throttle")) {
        //FileConfiguration config = plugin.getConfig();
        
        // Switch on Throttle
        if (args.length == 1 && args[0].equalsIgnoreCase("on")) {
          Player player = (Player) sender;
          // If it is already on do nothing
          if (forceHashMap.get(player) != null) {
            sender.sendMessage(ChatColor.AQUA+"Throttle has already been turned on.");
          }
          // Else do something; include player in playerSpeed and playerAcceleration hashmaps
          else {
            //speedHashMap.put(player, 0F);
            forceHashMap.put(player, 0f);
            modeHashMap.put(player, (byte) 0);
            sender.sendMessage(ChatColor.AQUA+"Throttle has been enabled.");
            
            brakeHashMap.put(player, Bukkit.createBossBar("Brake", BarColor.BLUE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC));
            brakeHashMap.get(player).addPlayer(player); /* Remove player from boss bar */
            // Store inventory
//            for (int i = 0; i < 41; i++){
//              config.set("inv."+player.getName()+"."+i, player.getInventory().getItem(i));
//            }
            ItemStack[] playerHB = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
              playerHB[i] = player.getInventory().getItem(i);
            }
            invHashMap.put(player, playerHB);
            //plugin.saveConfig();
          }
      
          // Make items into OldThrottle controls
          invItemsPg1(player);
          return true;
        }
    
        // Switch off throttle
        else if (args.length == 1 && args[0].equalsIgnoreCase("off")){
          Player player = (Player) sender;
          //but do nothing if throttle was already off
          if (forceHashMap.get(player) == null){
            sender.sendMessage(ChatColor.AQUA+"Throttle has already been turned off.");
          } else {
            //remove speed and force hashmap entry
            speedHashMap.remove(player);
            forceHashMap.remove(player);
            modeHashMap.remove(player);
  
            brakeHashMap.get(player).removePlayer(player); /* Remove player from boss bar */
            brakeHashMap.remove(player);
            //restore inventory
//            for (int i = 0; i < 41; i++){
//              player.getInventory().setItem(i, (ItemStack) config.get("inv."+player.getName()+"."+i));
//            }
  
            ItemStack[] playerHB = invHashMap.get(player);
            for (int i = 0; i < 9; i++) {
              player.getInventory().setItem(i, playerHB[i]);
            }
  
            //send message to player
            sender.sendMessage(ChatColor.AQUA+"Throttle turned off");
          }
          return true;
        }
      }
    }
    
    return false;
  }
  
  public void invItemsPg1(Player player) {
    // Clear inventory (if switching from Page 2)
    for (int i = 0; i < 9; i++) {
      player.getInventory().setItem(i, new ItemStack(Material.AIR, 0));
    }
  
    ItemStack item = new ItemStack(Material.BLUE_DYE, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.setDisplayName("Add Brake");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(0, item);
  
    item.setType(Material.LIME_DYE);
    itemMeta.setDisplayName("Off and Release");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(1, item);
  
    item.setType(Material.PINK_DYE);
    itemMeta.setDisplayName("Shunt");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(2, item);
  
    item.setType(Material.MAGENTA_DYE);
    itemMeta.setDisplayName("Series");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(3, item);
  
    item.setType(Material.PURPLE_DYE);
    itemMeta.setDisplayName("Parallel");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(4, item);
  
    item.setType(Material.ORANGE_DYE);
    itemMeta.setDisplayName("Auto Zone");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(5, item);
  
    item.setType(Material.RED_DYE);
    itemMeta.setDisplayName("Emergency Brake");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(6, item);
  
    item.setType(Material.LIGHT_GRAY_DYE);
    itemMeta.setDisplayName("More");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(8, item);
  }
  
  @EventHandler
  public void quit(PlayerQuitEvent event){
    Player player = event.getPlayer();
    if (speedHashMap.containsKey(player)) {
      speedHashMap.remove(player);
      forceHashMap.remove(player);
      modeHashMap.remove(player);
  
      brakeHashMap.get(player).removePlayer(player); /* Remove player from boss bar */
      brakeHashMap.remove(player);
      //restore inventory
      ItemStack[] playerHB = invHashMap.get(player);
      for (int i = 0; i < 9; i++) {
        player.getInventory().setItem(i, playerHB[i]);
      }
    }
  }
  
  @EventHandler
  public void cancelDrop(PlayerDropItemEvent event) {
    if (speedHashMap.containsKey(event.getPlayer())) event.setCancelled(true);
  }
  
  @EventHandler
  public void noDie(PlayerDeathEvent event) {
    if (speedHashMap.containsKey(event.getEntity())) {
      event.setKeepInventory(true);
    }
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Action action = event.getAction(); // Instance of action
    Player player = event.getPlayer(); // Instance of player.
    if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && speedHashMap.containsKey(player)) {
      if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
        String mode = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        CartProperties cp = CartProperties.getEditing(player);
        switch (mode){
          // Inv Page 1
          case "Add Brake":
            //modeHashMap.put(player, (byte) 0);
            if (brakeHashMap.get(player).getProgress() > 0.062) {
              brakeHashMap.get(player).setProgress(brakeHashMap.get(player).getProgress()-0.06);
              forceHashMap.put(player, forceHashMap.get(player)-0.001f);
            }
            break;
          case "Emergency Brake":
            modeHashMap.put(player, (byte) 0);
            while (brakeHashMap.get(player).getProgress() > 0.062) {
              brakeHashMap.get(player).setProgress(brakeHashMap.get(player).getProgress()-0.06);
              forceHashMap.put(player, forceHashMap.get(player)-0.001f);
            }
            break;
          case "Off and Release":
            if (speedHashMap.get(player) < 0.1f) player.performCommand("train launch 15");
            modeHashMap.put(player, (byte) 0);
            forceHashMap.put(player, 0f);
            break;
          case "Shunt":
            modeHashMap.put(player, (byte) 1);
            break;
          case "Series":
            modeHashMap.put(player, (byte) 2);
            break;
          case "Parallel":
            modeHashMap.put(player, (byte) 3);
            break;
          case "Auto Zone":
            modeHashMap.put(player, (byte) 0);
            speedHashMap.put(player, Float.valueOf(String.format("%.1f", speedHashMap.get(player))));
            forceHashMap.put(player, 0f);
            break;
          case "More":
            invItemsPg2(player);
            break;
          // Inv Page 2
          case "Less":
            invItemsPg1(player);
            break;
          case "Left":
            if (cp != null) {
              TrainProperties properties = cp.getTrainProperties();
              properties.addTags("Left");
            }
            break;
          case "Forward":
            if (cp != null) {
              TrainProperties properties = cp.getTrainProperties();
              properties.addTags("Forward");
            }
            break;
          case "Right":
            if (cp != null) {
              TrainProperties properties = cp.getTrainProperties();
              properties.addTags("Right");
            }
            break;
        }
      }
    }
  }
  
  public void invItemsPg2(Player player) {
    // Clear inventory (if switching from Page 1)
    for (int i = 0; i < 9; i++) {
      player.getInventory().setItem(i, new ItemStack(Material.AIR, 0));
    }
  
    ItemStack item = new ItemStack(Material.LIGHT_GRAY_DYE, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.setDisplayName("Less");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(0, item);
  
    item.setType(Material.LIME_DYE);
    itemMeta.setDisplayName("Left");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(2, item);
    
    item.setType(Material.ORANGE_DYE);
    itemMeta.setDisplayName("Forward");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(3, item);
    
    item.setType(Material.YELLOW_DYE);
    itemMeta.setDisplayName("Right");
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(4, item);
  }
  
  public void repeatThrottle() {
    for (Player player : forceHashMap.keySet()) {
      //get properties of the cart the player is currently editing
  
      CartProperties cartProperties = CartProperties.getEditing(player);
      //check if we got cart properties
      if (cartProperties != null && cartProperties.hasOwners() && cartProperties.getOwners().contains(player.getName())) {
        //if we can, get properties from the train the cart is part of
        TrainProperties properties = cartProperties.getTrainProperties();
    
        // accelerating forces
        // shunt
        if (modeHashMap.get(player) == (byte) 1) {
          if (speedHashMap.get(player) < 0.4) forceHashMap.put(player, 0.001f);
          else forceHashMap.put(player, 0f);
          if (brakeHashMap.get(player).getProgress() < 0.97) {
            brakeHashMap.get(player).setProgress(brakeHashMap.get(player).getProgress()+0.0026);
          }
        }
        // series
        else if (modeHashMap.get(player) == (byte) 2) {
          if (speedHashMap.get(player) < 0.2) forceHashMap.put(player, 0f);
          else if (speedHashMap.get(player) < 0.6) forceHashMap.put(player, 0.003f);
          else if (speedHashMap.get(player) < 0.9) forceHashMap.put(player, 0.002f);
          else forceHashMap.put(player, 0.001f);
        }
        // parallel
        else if (modeHashMap.get(player) == (byte) 3) {
          if (speedHashMap.get(player) < 0.5) forceHashMap.put(player, 0f);
          else if (speedHashMap.get(player) < 0.7) forceHashMap.put(player, 0.005f);
          else if (speedHashMap.get(player) < 1.0) forceHashMap.put(player, 0.004f);
          else if (speedHashMap.get(player) < 1.2) forceHashMap.put(player, 0.003f);
          else forceHashMap.put(player, 0.002f);
        }
        
        //save current speed in a variable to make working with it easier, and update it
        float currentSpeed = Math.max(speedHashMap.get(player)+forceHashMap.get(player), 0.0F);
        float torque = forceHashMap.get(player);
        //update speed limit of the train
        properties.setSpeedLimit(currentSpeed);
        //update new current speed
        speedHashMap.put(player, currentSpeed);
        //send speed to player
        String message = ChatColor.GREEN+"Speed: "+ChatColor.YELLOW+String.format("%.3f", currentSpeed)+ChatColor.GREEN+"m/t. Torque: "+ChatColor.YELLOW+String.format("%.3f", torque);
        //actionbar version
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        //boss bar version
        brakeHashMap.get(player).setTitle(message+" ↓ Air reservoir ↓");
      }
    }
  }
}
