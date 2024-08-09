package mikeshafter.mikestcaddons.throttle;

import org.bukkit.entity.Player;

public interface Throttle {

void onReceive (int bitArray);
void updateHUD (Player player);
}
