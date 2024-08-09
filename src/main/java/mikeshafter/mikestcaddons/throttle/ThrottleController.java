package mikeshafter.mikestcaddons.throttle;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import mikeshafter.mikestcaddons.MikesTCAddons;
import org.bukkit.entity.Player;

public class ThrottleController implements PacketListener {

private static final MikesTCAddons plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
private static final Player[] p = new Player[plugin.getServer().getMaxPlayers()];
private static final Throttle[] t = new Throttle[plugin.getServer().getMaxPlayers()];

public static void addThrottle (Player player, Throttle throttle) {
	for (int i = 0; i < p.length; i++) {
		if (p[i] == null) {
			p[i] = player;
			t[i] = throttle;
			break;
		}
	}
}

public static void removeThrottle (Player player) {
	for (int i = 0; i < p.length; i++) {
		if (p[i] == player) {
			p[i] = null;
			t[i] = null;
			break;
		}
	}
}

@Override
public void onPacketReceive (PacketReceiveEvent event) {
	Player player = event.getPlayer();
	if (getThrottle(player) == null) return;

	final var throttle = getThrottle(player);
	if (event.getType() != PacketType.IN_STEER_VEHICLE) return;
	float forwards = event.getPacket().read(PacketType.IN_STEER_VEHICLE.forwards);
	float sideways = event.getPacket().read(PacketType.IN_STEER_VEHICLE.sideways);
	boolean jump = event.getPacket().read(PacketType.IN_STEER_VEHICLE.jump);

	int bits = 0;
	// jump: 1, forwards: 2, backwards: 4, left: 8, right: 16
	bits |= jump ? 1 : 0;
	bits |= forwards > 0 ? 2 : 0;
	bits |= forwards < 0 ? 4 : 0;
	bits |= sideways > 0 ? 8 : 0;
	bits |= sideways < 0 ? 16 : 0;

	throttle.onReceive(bits);
	throttle.updateHUD(player);
}

public Throttle getThrottle (Player player) {
	for (int i = 0; i < p.length; i++) {
		if (p[i] == player) return t[i];
	}
	return null;
}

@Override
public void onPacketSend (PacketSendEvent event) {
}
}
