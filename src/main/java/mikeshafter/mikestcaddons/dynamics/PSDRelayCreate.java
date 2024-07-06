package mikeshafter.mikestcaddons.dynamics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class PSDRelayCreate implements Listener {
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		TextComponent t = (TextComponent) event.line(0);
		if (t != null && t.content().equalsIgnoreCase("[psd]")) {
			event.getPlayer().sendMessage(Component.text("You have created a PSD sign!"));
		}
	}
}
