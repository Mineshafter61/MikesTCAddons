package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;


public class Throttle {
  
  public static final ITrainProperty<Integer> POWER_CARS = new ITrainProperty<Integer>() {
    
    @PropertyParser("powerCars")
    public double parsePowerCars(PropertyParseContext<Integer> context) {
      return context.inputDouble();
    }
    
    @Override
    public Integer getDefault() {
      return 1;
    }
    
    @Override
    public Optional<Integer> readFromConfig(ConfigurationNode config) {
      return Util.getConfigOptional(config, "powerCars", int.class);
    }
    
    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Integer> value) {
      Util.setConfigOptional(config, "powerCars", value);
    }
  };
  double airUsed;
  ItemStack[] playerHB;
  Player player;
  MinecartGroup minecartGroup;
  int powerCars;
  int previous;
  int current;
  BossBar brakePipe;
  double power;
  
  public Throttle(Player player) {
    this.player = player;
    playerHB = new ItemStack[9];
    brakePipe = BossBar.bossBar(Component.text("Claim the train before driving!"), 1f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
    player.showBossBar(brakePipe);
    
    if (player.getVehicle() != null && MinecartGroupStore.get(player.getVehicle()) != null && MinecartGroupStore.get(player.getVehicle()).getProperties().getOwners().contains(player.getName().toLowerCase())) {
      minecartGroup = MinecartGroupStore.get(player.getVehicle());
      powerCars = minecartGroup.getProperties().get(POWER_CARS);
    } else powerCars = 0;
    
    airUsed = 0d;
    power = 0d;
    
    // Store inventory
    for (int i = 0; i < 9; i++) {
      playerHB[i] = player.getInventory().getItem(i);
    }
    
    // Set items
    setPlayerInventory(player, "Max Brake", Material.BLUE_DYE, 0);  // air valve open, maintain dynamic brake
    setPlayerInventory(player, "Med Brake", Material.CYAN_DYE, 1);  // air valve closed, maintain dynamic brake
    setPlayerInventory(player, "Min Brake", Material.LIGHT_BLUE_DYE, 2);  // release air, dynamic brake on
    setPlayerInventory(player, "Left", Material.ORANGE_DYE, 3);  // release air, neutral accel, add left tag
    setPlayerInventory(player, "Neutral", Material.LIME_DYE, 4);  // release air, neutral accel, remove all tags
    setPlayerInventory(player, "Right", Material.YELLOW_DYE, 5);  // release air, neutral accel, add right tag
    setPlayerInventory(player, "Shunt", Material.PINK_DYE, 6);  // positive shunt power
    setPlayerInventory(player, "Series", Material.MAGENTA_DYE, 7);  // power on 1 bogie
    setPlayerInventory(player, "Parallel", Material.PURPLE_DYE, 8);  // power on 2 bogies
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
  
      if (brakePipe.progress() < 0.098) brakePipe.progress(brakePipe.progress()+0.002f);
      previous = current;
      current = player.getInventory().getHeldItemSlot();  // current action
  
      switch (current) {
        // air valve open, maintain dynamic brake
        case 0 -> {
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
          //
          if (brakePipe.progress() > 0.01f) {
            airUsed += 3d;
            brakePipe.progress(brakePipe.progress()-0.012f);
          }
          power = -120d;
        }
        // air valve closed, maintain dynamic brake
        case 1 -> {
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
          //
          power = -120d;
        }
        // release air, dynamic brake on
        case 2 -> {
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
          //
          airUsed = 0d;
          power = -120d;
        }
        // release air, neutral accel, add left tag
        case 3 -> {
          airUsed = 0d;
          power = 0d;
          //
          minecartGroup.getProperties().addTags("left");
          minecartGroup.getProperties().removeTags("right");
        }
        // release air, neutral accel, remove all tags
        case 4 -> {
          airUsed = 0d;
          power = 0d;
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
        }
        // release air, neutral accel, add right tag
        case 5 -> {
          airUsed = 0d;
          power = 0d;
          //
          minecartGroup.getProperties().addTags("right");
          minecartGroup.getProperties().removeTags("left");
        }
        // positive shunt power
        case 6 -> {
          airUsed = 0d;
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
          //
          power = 30d;
        }
        // power on 1 bogie
        case 7 -> {
          airUsed = 0d;
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
          //
          power = 120d;
        }
        // power on 2 bogies
        case 8 -> {
          airUsed = 0d;
          minecartGroup.getProperties().removeTags("left");
          minecartGroup.getProperties().removeTags("right");
          //
          power = 240d;
        }
      }
  
  
      double speed = minecartGroup.getProperties().getSpeedLimit();
      double brakeForce = airUsed;
      double forwardForce = speed > 0.01 ? power/speed : 2500;
  
      // Reflect acceleration in speed change
      double acceleration = (forwardForce-brakeForce)*powerCars/minecartGroup.size();
      speed += (acceleration/36000);
  
      // Change speed limit as speed increases
      minecartGroup.getProperties().setSpeedLimit(speed);
  
      Component a = Component.text(String.format("%.4f m/t² |", acceleration/36000));
      a = a.color(TextColor.color(0, 255, 0));
      Component v = Component.text(String.format("| %.3f m/t %.2f km/h |", speed, speed*72));
      v = v.color(TextColor.color(0, 255, 255));
      Component barText = a.append(v);
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
