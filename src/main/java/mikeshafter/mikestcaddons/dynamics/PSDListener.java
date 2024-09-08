package mikeshafter.mikestcaddons.dynamics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class PSDListener implements Listener {

@EventHandler
public void onSignChange (SignChangeEvent event) {
	String t = parseComponent(event.line(0));
	if (t.equalsIgnoreCase("[psd]")) {
		event.getPlayer().sendMessage(Component.text("You have created a PSD sign!"));
	}
}

private String parseComponent (final Component c) {
	if (c instanceof TextComponent) {return ((TextComponent) c).content();} else if (c == null) {return "";} else return c.examinableName();
}

}
