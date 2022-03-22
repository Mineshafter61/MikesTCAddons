package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
  int previous;
  int current;
  boolean move;
  BossBar brakePipe;
  
  public Throttle(Player player) {
    this.player = player;
    playerHB = new ItemStack[9];
    brakePipe = BossBar.bossBar(Component.text("Claim the train before driving!"), 0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
    player.showBossBar(brakePipe);
    
    if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null && MinecartGroupStore.get(player.getVehicle()).getProperties().getOwners().contains(player.getName().toLowerCase())) {
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
    setPlayerInventory(player, "Release Brake Air", Material.CYAN_DYE, 2);
    setPlayerInventory(player, "Shunt", Material.PINK_DYE, 3);
    setPlayerInventory(player, "Series", Material.MAGENTA_DYE, 4);
    setPlayerInventory(player, "Parallel", Material.PURPLE_DYE, 5);
    setPlayerInventory(player, "Left", Material.ORANGE_DYE, 6);
    setPlayerInventory(player, "Right", Material.GREEN_DYE, 7);
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
  
      double traction = 370;
      previous = current;
      current = player.getInventory().getHeldItemSlot();  // current action
      switch (current) {
        case 0 -> {
          forwardPower.set(0);
          move = true;
        }
        case 1 -> {
          forwardPower.set(0);
          move = false;
        }
        case 2 -> airUsed.set(0d);
        case 3 -> {
          airUsed.set(0d);
          if (airRemaining.lessThan(8275)) {
            // add to remaining tank
            airRemaining.add(12);
            // half power
            forwardPower.set(12.5);
          } else {
            // full power
            forwardPower.set(25);
          }
          traction = 2500;
        }
        case 4 -> forwardPower.set(100);
        case 5 -> forwardPower.set(230);
        case 6 -> {
          minecartGroup.getProperties().addTags("left");
          minecartGroup.getProperties().removeTags("right");
        }
        case 7 -> {
          minecartGroup.getProperties().addTags("right");
          minecartGroup.getProperties().removeTags("left");
        }
      }
  
      // Move air from the reservoir into the brake pipe, and calculate forces
      moveAir();
      speed.set(Math.min(minecartGroup.getAverageForce(), minecartGroup.getProperties().getSpeedLimit()));
      double brakeForce = airUsed.get()*3;
      double forwardForce = speed.get() > 0.01 ? forwardPower.get()/speed.get() : 2500;
  
      // Brake pipe quick release valve opened as there was too much air
      if (brakePipe.progress() > 0.98) airUsed.set(0);
  
      // Reflect brake pipe air changes in the bossbar
      brakePipe.progress((float) (airUsed.get()/250));
  
      // Reflect acceleration in speed change
      double acceleration = (forwardForce-brakeForce)*powerCars/minecartGroup.size();
      if (speed.get() == 0 && acceleration < 0 || acceleration > traction) acceleration = 0;
      speed.add(acceleration/36000);
  
      // Play sounds
      if (previous != current) {  // Only run this once
        if (speed.lessThan(0.1) && current == 2)
          player.playSound(Sound.sound(Key.key("minecraft:train.accelerate"), Sound.Source.PLAYER, 5, 1), Sound.Emitter.self());
        else if (acceleration < 0 && speed.moreThan(0.1))
          player.playSound(Sound.sound(Key.key("minecraft:train.brake.automatic"), Sound.Source.PLAYER, 5, 1), Sound.Emitter.self());
        else if (acceleration < 0)
          player.playSound(Sound.sound(Key.key("minecraft:train.brake.mandraulic"), Sound.Source.PLAYER, 5, 1), Sound.Emitter.self());
      }
  
      player.playSound(Sound.sound(Key.key("minecraft:train.motion"), Sound.Source.PLAYER, (float) speed.get(), 1), Sound.Emitter.self());
  
      // Change speed limit as speed increases
      minecartGroup.getProperties().setSpeedLimit(Math.round(speed.get()*1000)/1000);
  
      Component p = Component.text(String.format("| %.1f kPa |", airRemaining.get()));
      p = p.color(TextColor.color(255, 0, 0));
      Component a = Component.text(String.format("| %.4f m/t² |", acceleration/36000));
      a = a.color(TextColor.color(0, 255, 0));
      Component v = Component.text(String.format("| %.3f m/t %.2f km/h |", speed.get(), speed.get()*72));
      v = v.color(TextColor.color(0, 255, 255));
      Component barText = p.append(a).append(v);
      brakePipe.name(barText);
    }
  }
  
  private void moveAir() {
    if (airRemaining.get() > 0 && move) {
      airRemaining.subtract(5);
      airUsed.add(5);
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
