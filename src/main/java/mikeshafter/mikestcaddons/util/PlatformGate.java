package mikeshafter.mikestcaddons.util;

import mikeshafter.mikestcaddons.MikesTCAddons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class PlatformGate {
  private static final List<PlatformGate> activeGates = new ArrayList<>();
  private final Block block;
  private final BlockData blockData;
  private final Location blockLoc;
  private final BlockFace openDirection;
  private final long openTime;
  private final Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
  private Entity armorStand;
  private Entity fallingBlock;
  private BukkitTask task = null;
  private boolean gateClosing = false;
  private int remainCount = 0;
  
  public PlatformGate(Block block, BlockFace openDirection, long openTime) {
    this.openTime = openTime;
    this.block = block;
    this.blockLoc = block.getLocation();
    this.openDirection = openDirection;
    this.blockData = block.getBlockData();
  }
  
  public static List<PlatformGate> getActiveGates() {
    return activeGates;
  }
  
  private BlockFace getOpenDirection() {
    return this.openDirection;
  }
  
  private long getOpenTime() {
    return this.openTime;
  }
  
  private Entity getArmorStand() {
    return this.armorStand;
  }
  
  public Block getBlock() {
    return this.block;
  }
  
  private void spawnFallingBlock() {
    this.armorStand = this.block.getWorld().spawn(this.blockLoc.add(0.5D, -1.4805D, 0.5D), org.bukkit.entity.ArmorStand.class, (stand) -> {
      stand.setVisible(false);
      stand.setGravity(false);
      stand.setInvulnerable(true);
    });
    this.fallingBlock = this.block.getWorld().spawnFallingBlock(this.blockLoc, this.blockData);
    this.armorStand.addPassenger(this.fallingBlock);
    this.fallingBlock.setInvulnerable(true);
    this.fallingBlock.setGravity(false);
    this.resetCountdown();
    FallingBlock fallingBlock = (FallingBlock) this.fallingBlock;
    fallingBlock.setDropItem(false);
  }
  
  private void killFallingSand() {
    this.block.getWorld()
        .getNearbyEntities(this.armorStand.getLocation(), 1, 1, 1,
            (entity) -> entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.FALLING_BLOCK)
        .forEach(Entity::remove);
    this.armorStand.remove();
    this.fallingBlock.remove();
  }
  
  private void doCloseGate() {
    this.teleportFallingSand(this.getArmorStand(), this.getOpenDirection().getOppositeFace().getDirection().multiply(0.025D), 40-this.remainCount, false);
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      this.killFallingSand();
      this.getBlock().setBlockData(this.blockData);
    }, (45-this.remainCount));
    activeGates.remove(this);
  }
  
  private void teleportFallingSand(Entity entity, Vector direction, int count, boolean canCancel) {
    if (canCancel && this.task.isCancelled()) {
      this.remainCount = count;
    } else {
      if (count > 0 && !entity.isDead()) {
        Location newLoc = entity.getLocation().add(direction);
        Entity passenger = entity.getPassengers().size() > 0 ? entity.getPassengers().get(0) : null;
        if (passenger == null) {
          return;
        }
        
        entity.removePassenger(passenger);
        entity.teleport(newLoc);
        entity.addPassenger(passenger);
        Bukkit.getScheduler().runTaskLater(plugin, () -> this.teleportFallingSand(entity, direction, count-1, canCancel), 1L);
      }
      
    }
  }
  
  public void activateGate() {
    activeGates.add(this);
    this.spawnFallingBlock();
    Bukkit.getScheduler().runTaskLater(plugin, () -> this.getBlock().setType(Material.AIR), 5L);
    this.task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
      this.gateClosing = true;
      this.doCloseGate();
    }, (this.getOpenTime()+45));
    this.teleportFallingSand(this.getArmorStand(), this.getOpenDirection().getDirection().multiply(0.025D), 40, true);
  }
  
  public void closeGate(boolean force) {
    if (!force) {
      if (this.task != null && !this.gateClosing && !this.task.isCancelled()) {
        this.task.cancel();
        this.doCloseGate();
      }
    } else {
      this.killFallingSand();
      this.getBlock().setBlockData(this.blockData);
    }
  }
  
  private void resetCountdown() {
    if (!this.fallingBlock.isDead()) this.fallingBlock.setTicksLived(1);
    Bukkit.getScheduler().runTaskLater(plugin, this::resetCountdown, 20L);
  }
}
