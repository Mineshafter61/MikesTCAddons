package mikeshafter.mikestcaddons.throttle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Throttle {

  double airUsed;
  ItemStack[] playerHB;
  Player player;
  MinecartGroup minecartGroup;
  int powerCars;
  int previous;
  int current;
  BossBar brakePipe;
  double power;
  
  public Throttle(Player player, int powerCars) {
    this.player = player;
    this.powerCars = powerCars;
    playerHB = new ItemStack[9];
    brakePipe = BossBar.bossBar(Component.text("Claim the train before driving!"), 0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
    player.showBossBar(brakePipe);
    
    if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null && MinecartGroupStore.get(player.getVehicle()).getProperties().getOwners().contains(player.getName().toLowerCase())) {
      minecartGroup = MinecartGroupStore.get(player.getVehicle());
      minecartGroup.getProperties().setSpeedLimit(0d);  // Stop the train so it doesn't go rouge
    }
  
    airUsed = 0d;
    power = 0d;
  
    // Store inventory
    for (int i = 0; i < 9; i++) {
      playerHB[i] = player.getInventory().getItem(i);
    }
  
    // Set items
    setPlayerInventory(player, "Left", Material.ORANGE_DYE, 0);  // release air, neutral accel, add left tag
    setPlayerInventory(player, "Max Brake", Material.BLUE_DYE, 1);  // air valve open, maintain dynamic brake
    setPlayerInventory(player, "Med Brake", Material.CYAN_DYE, 2);  // air valve closed, maintain dynamic brake
    setPlayerInventory(player, "Min Brake", Material.LIGHT_BLUE_DYE, 3);  // release air, dynamic brake on
    setPlayerInventory(player, "Neutral", Material.LIME_DYE, 4);  // release air, neutral accel, remove all tags
    setPlayerInventory(player, "Shunt", Material.PINK_DYE, 5);  // positive shunt power
    setPlayerInventory(player, "Series", Material.MAGENTA_DYE, 6);  // power on 1 bogie
    setPlayerInventory(player, "Parallel", Material.PURPLE_DYE, 7);  // power on 2 bogies
    setPlayerInventory(player, "Right", Material.YELLOW_DYE, 8);  // release air, neutral accel, add right tag
  }
  
  private void setPlayerInventory(Player player, String displayName, Material type, int slot) {
    ItemStack item = new ItemStack(type, 1);
    ItemMeta itemMeta = item.getItemMeta();
    assert itemMeta != null;
    itemMeta.displayName(Component.text(displayName));
    item.setItemMeta(itemMeta);
    player.getInventory().setItem(slot, item);
  }
  
  public void run() {
    if (minecartGroup.getProperties().getOwners().contains(player.getName().toLowerCase())) {
  
      previous = current;
      double speed = minecartGroup.getProperties().getSpeedLimit();  // get speed
      current = player.getInventory().getHeldItemSlot();  // current action
  
      switch (current) {
        // air valve open, maintain dynamic brake
        case 1 -> {
          //
          if (brakePipe.progress() > 0.01f) {
            airUsed += 0.0002;
            brakePipe.progress(brakePipe.progress()-0.006f);
          }
          power = 0d;
        }
        // air valve closed, maintain dynamic brake
        case 2 -> {
          //
          power = airUsed == 0d && speed > 0.15 ? (0.15-speed)/200 : 0d;
        }
        // release air, dynamic brake on
        case 3 -> {
          //
          airUsed = 0d;
          power = (0.15-speed)/200;
        }
        // release air, neutral accel, add left tag
        // release air, neutral accel, remove all tags
        case 4 -> {
          airUsed = 0d;
          power = 0d;
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
        }
        // positive shunt power
        case 5 -> {
          airUsed = 0d;
          if (brakePipe.progress() < 0.98) brakePipe.progress(brakePipe.progress()+0.002f);
          //
          power = 0.0005/(1+Math.pow(3, 10*speed-2));
          minecartGroup.setForwardForce(speed);
        }
        // power on 1 bogie
        case 6 -> {
          airUsed = 0d;
          //
          power = speed > 0.15 ? 0.001d : 0d;
        }
        // power on 2 bogies
        case 7 -> {
          airUsed = 0d;
          //
          power = speed > 0.15 ? 0.002d : 0d;
        }
        // left tag
        case 0 -> {
          //
          minecartGroup.getProperties().addTags("left");
          minecartGroup.getProperties().removeTags("right");
        }
        // right tag
        case 8 -> {
          //
          minecartGroup.getProperties().addTags("right");
          minecartGroup.getProperties().removeTags("left");
        }
      }
  
      
      double forwardForce;
      if (speed < 0.01 && power > 0) forwardForce = 0.07;
      else if (speed < 0.01) forwardForce = 0;
      else forwardForce = power/speed;
  
      // Reflect acceleration in speed change
      speed += (forwardForce) * powerCars / minecartGroup.size();
      speed -= airUsed;
      speed = speed < 0 ? 0 : speed;
  
      // Change speed limit as speed increases
      minecartGroup.setForwardForce(speed);
      minecartGroup.getProperties().setSpeedLimit(speed);
  
      Component p = Component.text(String.format("| %d/%d |", powerCars, minecartGroup.size()));
      p = p.color(TextColor.color(255, 0, 0));
      Component a = Component.text(String.format("| %.4f m/t² |", forwardForce*powerCars/minecartGroup.size()-airUsed));
      a = a.color(TextColor.color(0, 255, 0));
      Component v = Component.text(String.format("| %.3f m/t %.2f km/h |", speed, speed*72));
      v = v.color(TextColor.color(0, 255, 255));
      Component barText = p.append(a).append(v);
      brakePipe.name(barText);
    }
  }
  
  public void removePlayer() {
    // Restore inventory
    player.hideBossBar(brakePipe);
    for (int i = 0; i < 9; i++) player.getInventory().setItem(i, playerHB[i]);
  }
  
  public Player getPlayer() {
    return player;
  }
}
