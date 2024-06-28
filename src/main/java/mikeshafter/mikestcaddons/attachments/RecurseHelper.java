package mikeshafter.mikestcaddons.attachments;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

import java.util.Set;

public abstract class RecurseHelper {
	final MinecartMember<?> member;

	public RecurseHelper(MinecartMember<?> member) {
		this.member = member;
	}

	public void run() {
		ConfigurationNode fullConfig = this.member.getProperties().getModel().getConfig();

		ConfigurationNode attachmentsNode = fullConfig.getNode("attachments");
		Set<ConfigurationNode> attachmentsSet = attachmentsNode.getNodes();

		if (attachmentsSet != null) for (ConfigurationNode innerNode : attachmentsSet) {
			attachmentsNode.set(innerNode.getPath(), recurse(innerNode));
			fullConfig.set("attachments", attachmentsNode);
		}

		this.member.getProperties().getModel().update(fullConfig);
	}

	private ConfigurationNode recurse(ConfigurationNode node) {
		ConfigurationNode attachmentsNode = node.getNode("attachments");
		Set<ConfigurationNode> attachmentsSet = attachmentsNode.getNodes();

		if (attachmentsSet != null) for (ConfigurationNode innerNode : attachmentsSet) {
			attachmentsNode.set(innerNode.getPath(), recurse(innerNode));
			node.set("attachments", attachmentsNode);
		}

		return call(node);
	}

	protected abstract ConfigurationNode call(ConfigurationNode node);
}
