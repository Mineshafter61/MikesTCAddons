package mikeshafter.mikestcaddons.throttle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SimpleLever implements Throttle {

private final MinecartGroup controlledGroup;
private double speed;
private float gear = 0.0004f;
private float current;

public SimpleLever (MinecartGroup controlledGroup) {
	this.controlledGroup = controlledGroup;
}

@Override
public void onReceive (int bitArray) {
	// SimpleLever only uses A/D buttons to control the lever.
	// We get the 8's bit for A
	if ((bitArray & 8) != 0) {
		this.speed = this.controlledGroup.getAverageForce() - 0.002;
		// P = 0.5 mva
		// I = P/V
		current = (float) (speed * 0.002 / gear);
		if (current >= 0.8f) gear += 0.0002f;
		this.controlledGroup.setForwardForce(speed);
	}
	// We get the 16's bit for D
	if ((bitArray & 16) != 0) {
		this.speed = this.controlledGroup.getAverageForce() + 0.002;
		// P = 0.5 mva
		// I = P/V
		current = (float) (speed * 0.002 / gear);
		if (current <= 0.2f && gear > 0.0004f) gear -= 0.0002f;
		if (speed < 0f) speed = 0f;
		this.controlledGroup.setForwardForce(speed);
	}
	if (current < 0f) current = 0f;
}

@Override
public void updateHUD (Player player) {
	player.sendActionBar(Component.text(speed));
	var resr = BossBar.bossBar(Component.text("Main Reservoir").asComponent(), 0.75F, BossBar.Color.PINK, BossBar.Overlay.NOTCHED_10);
	var trac = BossBar.bossBar(Component.text("Traction Current").asComponent(), current, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
	player.showBossBar(resr);
	player.showBossBar(trac);
}

}
