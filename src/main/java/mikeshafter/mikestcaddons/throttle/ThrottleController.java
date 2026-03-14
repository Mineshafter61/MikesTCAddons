package mikeshafter.mikestcaddons.throttle;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import mikeshafter.mikestcaddons.MikesTCAddons;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class ThrottleController implements PacketListener {

private static final MikesTCAddons plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
private static final HashMap<Player, Throttle> throttleMap = new HashMap<>(plugin.getServer().getMaxPlayers());

public static void addThrottle (Player player, Throttle throttle) {
	throttleMap.put(player, throttle);
}

public static void run () {
	throttleMap.forEach((p, t) -> t.always(p));
}

@Override
public void onPacketReceive (PacketReceiveEvent event) {
	Player player = event.getPlayer();
	final Throttle throttle = getThrottle(player);
	if (throttle == null) return;

	if (event.getType() != PacketType.IN_STEER_VEHICLE) return;
	float forwards = event.getPacket().read(PacketType.IN_STEER_VEHICLE.forwards);
	float sideways = event.getPacket().read(PacketType.IN_STEER_VEHICLE.sideways);
	boolean jump = event.getPacket().read(PacketType.IN_STEER_VEHICLE.jump);
	boolean unmount = event.getPacket().read(PacketType.IN_STEER_VEHICLE.unmount);

	if (unmount) removeThrottle(player);

	throttle.onReceive(forwards, sideways, jump);
}

public Throttle getThrottle (Player player) {
	return throttleMap.get(player);
}

public static void removeThrottle (Player player) {
	throttleMap.remove(player);
}

@Override
public void onPacketSend (PacketSendEvent event) {
}
}