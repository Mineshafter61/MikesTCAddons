package mikeshafter.mikestcaddons.attachments;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import java.util.ArrayDeque;

public abstract class RecurseHelper {

final MinecartMember<?> member;

public RecurseHelper (MinecartMember<?> member) {this.member = member;}

public void run () {
	ArrayDeque<ConfigurationNode> stack = new ArrayDeque<>();
	ConfigurationNode node = this.member.getProperties().getModel().getConfig();
	stack.addFirst(node);
	while (!stack.isEmpty()) {
		stack.addAll(node.getNodeList("attachments"));
		call(node);
		node = stack.removeFirst();
	}
	this.member.getProperties().getModel().sync();
}

protected abstract void call (ConfigurationNode node);
}
