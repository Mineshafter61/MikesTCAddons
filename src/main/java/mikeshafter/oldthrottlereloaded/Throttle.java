package mikeshafter.oldthrottlereloaded;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;


public class Throttle {
  static ArrayList<Player> playerList = new ArrayList<>();
  static HashMap<Player, Double> airReservoir = new HashMap<>();
  static HashMap<Player, ItemStack[]> invHashMap = new HashMap<>();

  public static void addPlayer(Player player) {
    playerList.add(player);
    airReservoir.put(player, 0.1);
  
    // Store inventory
    ItemStack[] playerHB = new ItemStack[9];
    for (int i = 0; i < 9; i++) {
      playerHB[i] = player.getInventory().getItem(i);
    }
    invHashMap.put(player, playerHB);
    
    // Set items
    setPlayerInventory(player, "Brake Valve Open", Material.BLUE_DYE, 0);
    setPlayerInventory(player, "Release Brakes", Material.LIME_DYE, 1);
    setPlayerInventory(player, "Shunt", Material.PINK_DYE, 2);
    setPlayerInventory(player, "Series", Material.MAGENTA_DYE, 3);
    setPlayerInventory(player, "Parallel", Material.PURPLE_DYE, 4);
    setPlayerInventory(player, "Left", Material.ORANGE_DYE, 5);
    setPlayerInventory(player, "Right", Material.GREEN_DYE, 6);
  }

  public static void removePlayer(Player player) {
    playerList.remove(player);
    airReservoir.remove(player);
  
    // Restore inventory
    ItemStack[] playerHB = invHashMap.get(player);
    for (int i = 0; i < 9; i++) {
      player.getInventory().setItem(i, playerHB[i]);
    }
  }
  
  private static void setPlayerInventory(Player player, String displayName, Material type, int slot) {
    ItemStack item = new ItemStack(type, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.displayName(Component.text(displayName));
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(slot, item);
  }

  public static void throttleTask() {
    if (playerList != null)
    for (Player player : playerList) {
      MinecartGroup vehicle = MinecartGroupStore.get(player.getVehicle());
      TrainProperties properties = vehicle.getProperties();
      if (properties.getOwners().contains(player.getName().toLowerCase())) {
        
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        double force = 0d;
        
        // get speed and power cars
        double speed = vehicle.get(0).getRealSpeedLimited()*20;  // speed in m/s
        int powerCars = vehicle.size();  // Future change this to a property
  
        switch (heldItemSlot) {
          case 0 -> {
            if (airReservoir.get(player) < 5) airReservoir.put(player, airReservoir.get(player)+0.01);
            double pressure = airReservoir.get(player)*303.15/0.001;
            force = -1*pressure*0.137*0.060;
          }
          case 1 -> {
            force = 0;
            properties.removeTags("left", "right");
          }
          case 2 -> force = speed > 0.25 ? 13200/speed : 52800;
          case 3 -> force = 160000/speed;  // 80 kW power
          case 4 -> force = 640000/speed;  // 320 kW power
          case 5 -> {
            properties.addTags("left");
            properties.removeTags("right");
          }
          case 6 -> {
            properties.addTags("right");
            properties.removeTags("left");
          }
        }
        
        /*  animation player
           for (MinecartMember<?> cart : vehicle) {
              AnimationOptions options = new AnimationOptions();
              options.setName("door_L");
              options.setSpeed(0.5);
              cart.playNamedAnimation(options);
            }
         */
  
        // Calculate values
        double drag = 1.05*1.8375*speed*speed; // drag in N
        double traction = speed == 0 ? 119040*vehicle.size() : 88320*vehicle.size(); // wheel slips (0.31*16*24000 : 0.23*16*24000)
        double friction = speed == 0 ? 30720*vehicle.size() : 19200*vehicle.size(); // resistive force (0.08*16*24000 : 0.05*16*24000)
        double drive = force*1.63*powerCars; // real thrust value. IRL value must be multiplied by 1.63 due to Minecraft physics.
  
        // set calculated force
        double resultant = (Math.abs(drive-friction-drag) < traction ? drive-friction-drag : 0)/400;
        // vehicle.setForwardForce(resultant/24000);
        vehicle.getProperties().setSpeedLimit(vehicle.getProperties().getSpeedLimit()+resultant/24000);
        System.out.println(ChatColor.GREEN+"| R "+ChatColor.YELLOW+resultant);
        // debug
        Component debug = Component.text(ChatColor.GREEN+"| R "+ChatColor.YELLOW+resultant);
        player.sendActionBar(debug);
  
      } else {
        removePlayer(player);
      }
      
      

    }
  }
}
