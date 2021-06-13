package mikeshafter.oldthrottle;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class OldThrottle extends JavaPlugin implements Listener{
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"OldThrottle by Mineshafter61: 1.16.5r5");
    getConfig().options().copyDefaults(true);
    saveConfig();
  
    // Initialise classes
    Throttle throttle = new Throttle();
    TrainAnnounce trainAnnounce = new TrainAnnounce();
  
    // Register Throttle and repeat it
    getServer().getPluginManager().registerEvents(throttle, this);
    Objects.requireNonNull(getCommand("throttle")).setExecutor(throttle);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, throttle::repeatThrottle, 0L, 1L);
  
    // Register train announcers
    Objects.requireNonNull(getCommand("ta")).setExecutor(trainAnnounce);
    Objects.requireNonNull(getCommand("tj")).setExecutor(trainAnnounce);
  }
  
  @Override
  public void onDisable(){
    // Plugin shutdown logic
    saveConfig();
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"OldThrottle has been disabled.");
  }
}
