package mikeshafter.mikestcaddons.attachments;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import java.util.ArrayDeque;

public abstract class RecurseHelper {

final MinecartMember<?> member;

public RecurseHelper (MinecartMember<?> member) {this.member = member;}

	public synchronized void run () {
	ArrayDeque<ConfigurationNode> stack = new ArrayDeque<>();
		stack.addFirst(this.member.getProperties().getModel().getConfig());
	while (!stack.isEmpty()) {
		ConfigurationNode node = stack.removeFirst();
		stack.addAll(node.getNodeList("attachments"));
		call(node);
	}
	this.member.getProperties().getModel().sync();
}

protected abstract void call (ConfigurationNode node);
}