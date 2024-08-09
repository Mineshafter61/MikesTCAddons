package mikeshafter.mikestcaddons.throttle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ManGear implements Throttle {

private final MinecartGroup controlledGroup;
private double speed;
private float gear = 0.0004f;
private float current;

public ManGear (MinecartGroup controlledGroup) {
	this.controlledGroup = controlledGroup;
}

@Override
public void onReceive (int bitArray) {
	// SimpleLever only uses A/D buttons to control the lever.
	// We get the 2's bit for W
	if ((bitArray & 2) != 0) {
		this.speed = this.controlledGroup.getAverageForce() + 0.002;
		// P = 0.5 mva
		// I = P/V
		current = (float) (speed * 0.002 / gear);
		this.controlledGroup.setForwardForce(speed);
	}
	// We get the 16's bit for S
	if ((bitArray & 4) != 0) {
		this.speed = this.controlledGroup.getAverageForce() - 0.002;
		// P = 0.5 mva
		// I = P/V
		current = (float) (speed * 0.002 / gear);
		this.controlledGroup.setForwardForce(speed);
	}
	if ((bitArray & 8) != 0) {
		gear += 0.0002f;
	}
	if ((bitArray & 16) != 0) {
		if (gear > 0.0004f) gear -= 0.0002f;
	}
}

@Override
public void updateHUD (Player player) {
	player.sendActionBar(Component.text(speed));
	BossBar.bossBar(Component.text("Main Reservoir").asComponent(), 0.75F, BossBar.Color.PINK, BossBar.Overlay.NOTCHED_10);
	BossBar.bossBar(Component.text("Traction Current").asComponent(), current, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
}

}
