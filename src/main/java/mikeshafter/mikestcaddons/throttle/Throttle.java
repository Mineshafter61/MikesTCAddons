package mikeshafter.mikestcaddons.throttle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import mikeshafter.mikestcaddons.PositiveDouble;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Throttle {
  
  PositiveDouble speed;
  PositiveDouble airUsed;
  PositiveDouble airRemaining;
  PositiveDouble forwardPower;
  ItemStack[] playerHB;
  Player player;
  MinecartGroup minecartGroup;
  int powerCars;
  
  public Throttle(Player player) {
    this.player = player;
    playerHB = new ItemStack[9];
    
    if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null) {
      minecartGroup = MinecartGroupStore.get(player.getVehicle());
      powerCars = minecartGroup.size();
    } else powerCars = 0;
    
    speed = new PositiveDouble(0);
    airUsed = new PositiveDouble(0);
    airRemaining = new PositiveDouble(0);
    forwardPower = new PositiveDouble(0);
    
    // Store inventory
    for (int i = 0; i < 9; i++) {
      playerHB[i] = player.getInventory().getItem(i);
    }
    
    // Set items
    setPlayerInventory(player, "Brake Valve Open", Material.BLUE_DYE, 0);
    setPlayerInventory(player, "Brake Valve Close", Material.LIME_DYE, 1);
    setPlayerInventory(player, "Shunt", Material.PINK_DYE, 2);
    setPlayerInventory(player, "Series", Material.MAGENTA_DYE, 3);
    setPlayerInventory(player, "Parallel", Material.PURPLE_DYE, 4);
    setPlayerInventory(player, "Left", Material.ORANGE_DYE, 5);
    setPlayerInventory(player, "Right", Material.GREEN_DYE, 6);
  }
  
  public void run() {
    switch (player.getInventory().getHeldItemSlot()) {
      case 0:
        airRemaining.subtract(15);
        if (airRemaining.get() > 0) airUsed.add(15);
        break;
      case 1:
      
      case 2:
        airUsed.set(0d);
        if (airRemaining.lessThan(8273)) {
          // add to remaining tank
          airRemaining.add(10);
          // half power
          forwardPower.set(37.5);
        } else {
          // full power
          forwardPower.set(75);
        }
        break;
      case 3:
        forwardPower.set(100);
      case 4:
        forwardPower.set(175);
      case 5:
        minecartGroup.getProperties().addTags("left");
        minecartGroup.getProperties().removeTags("right");
      case 6:
        minecartGroup.getProperties().addTags("right");
        minecartGroup.getProperties().removeTags("left");
    }
    
    speed.set(minecartGroup.getAverageForce());
    double brakeForce = airUsed.get()*3;
    double forwardForce = forwardPower.get()/speed.get() < 370 ? forwardPower.get()/speed.get() : 0;
    
    double acceleration = (forwardForce-brakeForce)*powerCars/minecartGroup.size();
    if (speed.lessThanOrEquals(0) && acceleration < 0) acceleration = 0;
    
    Component message = Component.text(speed.get());
    player.sendActionBar(message);
  }
  
  public void removePlayer() {
    // Restore inventory
    for (int i = 0; i < 9; i++) player.getInventory().setItem(i, playerHB[i]);
  }
  
  public Player getPlayer() {
    return player;
  }
  
  private void setPlayerInventory(Player player, String displayName, Material type, int slot) {
    ItemStack item = new ItemStack(type, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.displayName(Component.text(displayName));
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(slot, item);
  }
}
