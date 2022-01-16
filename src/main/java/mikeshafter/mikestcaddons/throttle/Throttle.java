package mikeshafter.mikestcaddons.throttle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import mikeshafter.mikestcaddons.PositiveDouble;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;


public class Throttle {
  
  PositiveDouble speed;
  double force;
  ItemStack[] playerHB;
  Player player;
  
  public Throttle(Player player) {
    this.player = player;
    playerHB = new ItemStack[9];
    
    if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null) {
      speed = new PositiveDouble(player.getVelocity().distance(new Vector()));
    } else speed = new PositiveDouble(0);
    
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
  
  private void setPlayerInventory(Player player, String displayName, Material type, int slot) {
    ItemStack item = new ItemStack(type, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.displayName(Component.text(displayName));
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(slot, item);
  }
  
  public void removePlayer() {
    // Restore inventory
    for (int i = 0; i < 9; i++) {
      player.getInventory().setItem(i, playerHB[i]);
    }
  }
  
  public Player getPlayer() {
    return player;
  }
}
