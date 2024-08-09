package mikeshafter.mikestcaddons.attachments;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

public abstract class RecurseHelper {

final MinecartMember<?> member;

public RecurseHelper (MinecartMember<?> member) {this.member = member;}

public void run () {
	var fullConfig = this.member.getProperties().getModel().getConfig();

	var attachmentsNode = fullConfig.getNode("attachments");
	var attachmentsSet = attachmentsNode.getNodes();

	if (attachmentsSet != null) {
		for (var innerNode : attachmentsSet) {
			attachmentsNode.set(innerNode.getPath(), recurse(innerNode));
			fullConfig.set("attachments", attachmentsNode);
		}
	}

	this.member.getProperties().getModel().sync();
}

private ConfigurationNode recurse (ConfigurationNode node) {
	var attachmentsNode = node.getNode("attachments");
	var attachmentsSet = attachmentsNode.getNodes();

	if (attachmentsSet != null) {
		for (var innerNode : attachmentsSet) {
			attachmentsNode.set(innerNode.getPath(), recurse(innerNode));
			node.set("attachments", attachmentsNode);
		}
	}

	return call(node);
}

protected abstract ConfigurationNode call (ConfigurationNode node);
}
