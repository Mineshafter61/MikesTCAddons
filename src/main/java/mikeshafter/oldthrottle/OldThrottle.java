package mikeshafter.oldthrottle;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class OldThrottle extends JavaPlugin {
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"OldThrottle by Mineshafter61: 1.16.5r5");
    getConfig().options().copyDefaults(true);
    saveConfig();
    
    // Initialise classes
    Throttle throttle = new Throttle();
    TrainAnnounce trainAnnounce = new TrainAnnounce();
  
    // Register train announcers
    Objects.requireNonNull(getCommand("ta")).setExecutor(trainAnnounce);
    Objects.requireNonNull(getCommand("tj")).setExecutor(trainAnnounce);
  
    // Register Throttle and repeat it
    Objects.requireNonNull(getCommand("throttle")).setExecutor(throttle);
    getServer().getPluginManager().registerEvents(throttle, this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, throttle::repeatThrottle, 0L, 1L);
  }
  
  @Override
  public void onDisable(){
    // Plugin shutdown logic
    saveConfig();
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"OldThrottle has been disabled.");
  }
}
