package yjjcoolcool.simplethrottle;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SimpleThrottle extends JavaPlugin implements Listener{
  
  @Override
  public void onEnable() {
    // Plugin startup logic
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"SimpleThrottle starting...");
    getConfig().options().copyDefaults(true);
    saveConfig();
  
    Throttle throttle = new Throttle();
    getServer().getPluginManager().registerEvents(throttle, this);
    Objects.requireNonNull(getCommand("throttle")).setExecutor(throttle);
  
    getServer().getScheduler().scheduleSyncRepeatingTask(this, throttle::repeatThrottle, 0L, 1L);
  }
  
  @Override
  public void onDisable(){
    // Plugin shutdown logic
    saveConfig();
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"SimpleThrottle has been disabled.");
  }
}
