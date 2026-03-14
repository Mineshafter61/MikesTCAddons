package mikeshafter.mikestcaddons.throttle;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Test implements Throttle {

private final MinecartGroup controlledGroup;
private float forwards, sideways;
private boolean jump;
private int state = 0;
private double speed = 0;

public Test (MinecartGroup controlledGroup) {
	this.controlledGroup = controlledGroup;
}

@Override
public void onReceive (float forwards, float sideways, boolean jump) {
	this.forwards = forwards;
	this.sideways = sideways;
	this.jump = jump;

	/*
	 IMPORTANT: Minecraft now only sends 2 packets when u hold down any key:
		Press W: forwards = 0.98
		Hold W: nothing sent!
		Release W: forwards = 0.00
	 */

	if (forwards > 0) {
		// accelerate state
		this.state = 1;
	}
	else if (forwards < 0) {
		// brake state
		this.state = -1;
	}
	else {
		this.state = 0;
	}
}

@Override
public void always (Player player) {
	speed += this.state * 0.002d;
	controlledGroup.setForwardForce(speed);
	player.sendActionBar(Component.text("Speed:" + speed + " F:" + forwards + " S:" + sideways + " J:" + jump));
}
}