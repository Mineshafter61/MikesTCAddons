package mikeshafter.mikestcaddons.throttle;

import org.bukkit.entity.Player;

public interface Throttle {

void onReceive (float forwards, float sideways, boolean jump);
void always (Player player);
}